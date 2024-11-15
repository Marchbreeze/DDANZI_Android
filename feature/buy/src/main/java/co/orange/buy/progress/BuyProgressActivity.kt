package co.orange.buy.progress

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.orange.buy.BuildConfig.IAMPORT_CODE
import co.orange.buy.databinding.ActivityBuyProgressBinding
import co.orange.buy.finished.BuyFinishedActivity
import co.orange.core.R
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.base.BaseActivity
import co.orange.core.extension.breakLines
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.setPriceForm
import co.orange.core.extension.stringOf
import co.orange.core.extension.toPhoneFrom
import co.orange.core.extension.toast
import co.orange.core.navigation.NavigationManager
import co.orange.core.state.UiState
import co.orange.domain.entity.response.AddressInfoModel
import co.orange.domain.entity.response.BuyProgressModel
import coil.load
import com.iamport.sdk.domain.core.Iamport
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject
import co.orange.buy.R as featureR

@AndroidEntryPoint
class BuyProgressActivity :
    BaseActivity<ActivityBuyProgressBinding>(featureR.layout.activity_buy_progress) {
    @Inject
    lateinit var navigationManager: NavigationManager

    private val viewModel by viewModels<BuyProgressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initExitBtnListener()
        initDeliveryChangeBtnListener()
        initTermDetailBtnListener()
        initConfirmBtnListener()
        observeGetBuyDataState()
        observePostPayStartState()
        observePatchPayEndState()
        observePostOrderState()
    }

    override fun onResume() {
        super.onResume()

        if (!viewModel.isOrderStarted) getIntentInfo()
    }

    private fun initView() {
        binding.vm = viewModel
        Iamport.init(this)
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener {
            AmplitudeManager.trackEvent("click_purchase_quit")
            finish()
        }
    }

    private fun initDeliveryChangeBtnListener() {
        with(binding) {
            btnChangeDelivery.setOnSingleClickListener { navigateToAddAddress() }
            btnDeliveryAdd.setOnSingleClickListener { navigateToAddAddress() }
        }
    }

    private fun navigateToAddAddress() {
        AmplitudeManager.trackEvent("click_purchase_address")
        navigationManager.toDeliveryView(this)
    }

    private fun initTermDetailBtnListener() {
        with(binding) {
            btnTermServiceDetail.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_TERM_SERVICE)).apply {
                    startActivity(this)
                }
            }
            btnTermPurchaseDetail.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_TERM_PURCHASE)).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun initConfirmBtnListener() {
        binding.btnConfirmPurchase.setOnSingleClickListener {
            AmplitudeManager.trackEvent(
                "click_purchase_purchase",
                mapOf("product_id" to viewModel.productId),
            )
            viewModel.postPayStartToServer()
        }
    }

    private fun getIntentInfo() {
        with(viewModel) {
            if (productId.isEmpty()) productId = intent.getStringExtra(EXTRA_PRODUCT_ID).orEmpty()
            optionList =
                intent.getIntegerArrayListExtra(EXTRA_OPTION_LIST)?.mapNotNull { it?.toLong() }
                    ?: emptyList()
            getBuyDataFromServer()
        }
        AmplitudeManager.trackEvent("view_purchase", mapOf("product_id" to viewModel.productId))
    }

    private fun observeGetBuyDataState() {
        viewModel.getBuyDataState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> setBuyProgressUi(state.data)

                    is UiState.Failure -> {
                        toast(stringOf(R.string.error_msg))
                        finish()
                    }

                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun setBuyProgressUi(item: BuyProgressModel) {
        with(binding) {
            tvConfirmProductName.text = item.productName
            ivConfirmProduct.load(item.imgUrl)
            tvConfirmProductPrice.text = item.totalPrice.setPriceForm()
            tvConfirmPriceMoney.text = item.originPrice.setPriceForm()
            tvConfirmPriceDiscount.text =
                getString(R.string.add_minus, item.discountPrice.setPriceForm())
            tvConfirmPriceCharge.text =
                getString(R.string.add_plus, item.charge.setPriceForm())
            tvConfirmPriceTotal.text = item.totalPrice.setPriceForm()
        }
        setAddress(item.addressInfo)
    }

    private fun setAddress(address: AddressInfoModel) {
        if (address.recipient != null) {
            with(binding) {
                btnDeliveryAdd.isVisible = false
                layoutDeliveryItem.isVisible = true
                tvDeliveryName.text = address.recipient
                tvDeliveryAddress.text =
                    getString(
                        R.string.address_short_format,
                        address.zipCode,
                        address.address,
                    ).breakLines()
                tvDeliveryPhone.text = address.recipientPhone?.toPhoneFrom()
            }
        }
    }

    private fun observePostPayStartState() {
        viewModel.postPayStartState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> startIamportPurchase()
                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun startIamportPurchase() {
        val request = viewModel.createIamportRequest()
        if (request == null) {
            toast(stringOf(R.string.error_msg))
            return
        }
        Timber.tag("okhttp").d("IAMPORT PURCHASE REQUEST : $request")
        Iamport.payment(
            userCode = IAMPORT_CODE,
            iamPortRequest = request,
        ) { response ->
            Timber.tag("okhttp").d("IAMPORT PURCHASE RESPONSE : $response")
            if (response == null) return@payment
            viewModel.patchPayEndToServer(response.success)
        }
    }

    private fun observePatchPayEndState() {
        viewModel.patchPayEndState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> viewModel.postToRequestOrderToServer()
                    is UiState.Failure -> toast(stringOf(R.string.buy_order_error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun observePostOrderState() {
        viewModel.postOrderState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> navigateToPushOrFinish(state.data)
                    is UiState.Failure -> toast(stringOf(R.string.buy_order_error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun navigateToPushOrFinish(orderId: String) {
        if (isPermissionNeeded()) {
            navigationManager.toAlarmRequestViewWithIntent(this, true, orderId, null, null, null, null)
        } else {
            AmplitudeManager.updateProperty("user_push", "enabled")
            startActivity(BuyFinishedActivity.createIntent(this, orderId))
        }
        finish()
    }

    private fun isPermissionNeeded(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

    override fun onDestroy() {
        super.onDestroy()

        Iamport.close()
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID"
        private const val EXTRA_OPTION_LIST = "EXTRA_OPTION_LIST"

        const val WEB_TERM_SERVICE =
            "https://brawny-guan-098.notion.site/faa1517ffed44f6a88021a41407ed736?pvs=4"
        const val WEB_TERM_PURCHASE =
            "https://brawny-guan-098.notion.site/56bcbc1ed0f3454ba08fa1070fa5413d?pvs=4"

        @JvmStatic
        fun createIntent(
            context: Context,
            productId: String,
            optionList: ArrayList<Int>,
        ): Intent =
            Intent(context, BuyProgressActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
                putIntegerArrayListExtra(EXTRA_OPTION_LIST, optionList)
            }
    }
}
