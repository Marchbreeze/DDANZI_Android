package co.orange.main.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.orange.core.R
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.base.BaseActivity
import co.orange.core.extension.breakLines
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.setOnSingleClickShortListener
import co.orange.core.extension.setOverThousand
import co.orange.core.extension.setPriceForm
import co.orange.core.extension.stringOf
import co.orange.core.extension.toast
import co.orange.core.navigation.NavigationManager
import co.orange.core.state.UiState
import co.orange.domain.entity.response.ProductDetailModel
import co.orange.main.databinding.ActivityDetailBinding
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import co.orange.main.R as featureR

@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding>(featureR.layout.activity_detail) {
    @Inject
    lateinit var navigationManager: NavigationManager

    private val viewModel by viewModels<DetailViewModel>()

    private var optionBottomSheet: OptionBottomSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AmplitudeManager.plusIntProperty("user_looking", 1)
        initBackBtnListener()
        initDetailViewBtnListener()
        initLikeBtnListener()
        initPurchaseBtnListener()
        getIntentInfo()
        observeGetProductDetailState()
        observeLikeState()
    }

    private fun initBackBtnListener() {
        binding.btnBack.setOnSingleClickListener {
            AmplitudeManager.trackEvent("click_detail_quit")
            navigateBackWithIntent()
        }
        binding.btnHome.setOnSingleClickListener {
            AmplitudeManager.trackEvent("click_detail_home")
            navigateBackWithIntent()
        }
        onBackPressedDispatcher.addCallback(this) { navigateBackWithIntent() }
    }

    private fun navigateBackWithIntent() {
        if (viewModel.originLikeState != viewModel.likeState.value) {
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(EXTRA_IS_LIKED, viewModel.likeState.value),
            )
        }
        finish()
    }

    private fun initDetailViewBtnListener() {
        binding.btnDetailView.setOnSingleClickListener {
            if (viewModel.infoUrl.isNotEmpty()) {
                AmplitudeManager.trackEvent("click_detail_detail")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.infoUrl)))
            }
        }
    }

    private fun initLikeBtnListener() {
        binding.btnLike.setOnSingleClickShortListener {
            AmplitudeManager.trackEvent("click_detail_heart")
            if (!viewModel.getUserLogined()) {
                navigationManager.toLoginView(this, "like")
                return@setOnSingleClickShortListener
            }
            viewModel.setLikeStateWithServer()
        }
    }

    private fun initPurchaseBtnListener() {
        binding.btnPurchase.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                "click_detail_purchase",
                mapOf("product_id" to viewModel.productId),
            )
            if (!viewModel.getUserLogined()) {
                navigationManager.toLoginView(this, "buy")
                return@setOnSingleClickListener
            }
            if (viewModel.optionList.isEmpty()) {
                navigationManager.toBuyProgressView(this, viewModel.productId, arrayListOf())
            } else {
                optionBottomSheet = OptionBottomSheet()
                optionBottomSheet?.show(supportFragmentManager, BOTTOM_SHEET_OPTION)
            }
        }
    }

    private fun getIntentInfo() {
        with(viewModel) {
            productId = intent.getStringExtra(EXTRA_PRODUCT_ID).orEmpty()
            getProductDetailFromServer()
        }
        AmplitudeManager.trackEvent("view_detail", mapOf("product_id" to viewModel.productId))
    }

    private fun observeGetProductDetailState() {
        viewModel.getProductDetailState.flowWithLifecycle(lifecycle).onEach { state ->
            when (state) {
                is UiState.Success -> setProduct(state.data)
                is UiState.Failure -> toast(stringOf(R.string.error_msg))
                else -> return@onEach
            }
        }.launchIn(lifecycleScope)
    }

    private fun setProduct(item: ProductDetailModel) {
        with(binding) {
            layoutDetailBefore.isVisible = false
            tvDetailTitle.text = item.name.breakLines()
            chipsDetailCategory.text = item.category
            chipsDetailOption.isVisible = item.isOptionExist
            chipsDetailImminent.isVisible = item.isImminent
            tvDetailDiscountRate.text = item.discountRate.toString()
            tvDetailStockCount.text = item.stockCount.toString()
            ivDetailLike.isSelected = item.isInterested
            tvDetailLike.text = item.interestCount.setOverThousand()
            ivDetailProduct.load(item.imgUrl)
            tvDetailRealPrice.apply {
                text = item.originPrice.setPriceForm()
                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            tvDetailNowPrice.text = item.salePrice.setPriceForm()
        }
    }

    private fun observeLikeState() {
        viewModel.likeState.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach { isLiked ->
            with(binding) {
                ivDetailLike.isSelected = isLiked
                tvDetailLike.text = viewModel.interestCount.setOverThousand()
            }
        }.launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()

        optionBottomSheet = null
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID"
        const val EXTRA_IS_LIKED = "EXTRA_IS_LIKED"

        private const val BOTTOM_SHEET_OPTION = "BOTTOM_SHEET_OPTION"

        @JvmStatic
        fun createIntent(
            context: Context,
            productId: String,
        ): Intent =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
            }
    }
}
