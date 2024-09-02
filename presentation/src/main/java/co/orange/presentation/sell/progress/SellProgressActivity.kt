package co.orange.presentation.sell.progress

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.orange.core.base.BaseActivity
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.setPriceForm
import co.orange.core.extension.stringOf
import co.orange.core.extension.toast
import co.orange.core.state.UiState
import co.orange.domain.entity.response.SellProductModel
import co.orange.presentation.sell.push.SellPushActivity
import co.orange.presentation.setting.SettingActivity.Companion.WEB_TERM_SELL
import co.orange.presentation.setting.SettingActivity.Companion.WEB_TERM_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivitySellProgressBinding

@AndroidEntryPoint
class SellProgressActivity :
    BaseActivity<ActivitySellProgressBinding>(R.layout.activity_sell_progress) {
    private val viewModel by viewModels<SellProgressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initExitBtnListener()
        initTermDetailBtnListener()
        initConfirmBtnListener()
        initDateBtnListener()
        getProductWithIdFromIntent()
        observeGetProductState()
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener { finish() }
    }

    private fun initTermDetailBtnListener() {
        with(binding) {
            btnTermServiceDetail.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_TERM_SERVICE)).apply {
                    startActivity(this)
                }
            }
            btnTermSellDetail.setOnSingleClickListener {
                Intent(Intent.ACTION_VIEW, Uri.parse(WEB_TERM_SELL)).apply {
                    startActivity(this)
                }
            }
        }
    }

    private fun initConfirmBtnListener() {
        binding.btnConfirmSell.setOnSingleClickListener {
            // TODO 계좌 확인 & 구매 요청 서버통신 이후
            SellPushActivity.createIntent(this, -1).apply {
                startActivity(this)
            }
        }
    }

    private fun initDateBtnListener() {
        // TODO 데이트피커 구현
        // tvSellDate.text = "2024.06.28"
    }

    private fun getProductWithIdFromIntent() {
        with(viewModel) {
            productId = intent.getStringExtra(EXTRA_PRODUCT_ID).orEmpty()
            getProductWIthId()
        }
    }

    private fun observeGetProductState() {
        viewModel.getProductState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> setIntentUi(state.data)
                    is UiState.Failure -> {
                        toast(stringOf(R.string.error_msg))
                        finish()
                    }

                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }

    private fun setIntentUi(item: SellProductModel) {
        with(binding) {
            tvSellInfoName.text = item.productName
            tvSellInfoOriginPrice.text = item.originPrice.setPriceForm()
            tvSellInfoSellPrice.text = item.salePrice.setPriceForm()
            // TODO
            // ivSellProduct.load(R.drawable.mock_img_product)
        }
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID"

        @JvmStatic
        fun createIntent(
            context: Context,
            productId: String,
        ): Intent =
            Intent(context, SellProgressActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
            }
    }
}
