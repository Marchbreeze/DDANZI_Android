package co.orange.presentation.sell.info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import co.orange.domain.entity.response.SellDetailModel
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.core.base.BaseActivity
import kr.genti.core.extension.breakLines
import kr.genti.core.extension.setNumberForm
import kr.genti.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ActivitySellInfoBinding

@AndroidEntryPoint
class SellInfoActivity :
    BaseActivity<ActivitySellInfoBinding>(R.layout.activity_sell_info) {
    private val viewModel by viewModels<SellInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initExitBtnListener()
        initFixPurchaseBtnListener()
        getIntentInfo()
        setIntentUi(viewModel.mockSellInfo)
    }

    private fun initExitBtnListener() {
        binding.btnExit.setOnSingleClickListener { finish() }
    }

    private fun initFixPurchaseBtnListener() {
        // TODO
        binding.btnFixSell.setOnSingleClickListener { }
    }

    private fun getIntentInfo() {
        viewModel.productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1)
    }

    private fun setIntentUi(item: SellDetailModel) {
        with(binding) {
            tvInfoTransaction.text = getString(R.string.transaction_id, item.orderId).breakLines()
            ivInfoProduct.load(item.imgUrl)
            tvInfoProductName.text = item.productName
            tvInfoProductPrice.text = item.originPrice.setNumberForm()
            tvInfoBuyerNickname.text = item.buyerNickname
            tvInfoDeliveryName.text = item.addressInfo[0].recipient
            tvInfoDeliveryAddress.text =
                getString(
                    R.string.address_format,
                    item.addressInfo[0].zipCode,
                    item.addressInfo[0].address,
                ).breakLines()
            tvInfoDeliveryPhone.text = item.addressInfo[0].phone
            tvInfoTransactionMethod.text = item.paymentInfo[0].method
            tvInfoTransactionDate.text = item.paymentInfo[0].completedAt
            tvInfoPayKakao.text = item.originPrice.setNumberForm()
            tvInfoPayReal.text = item.salePrice.setNumberForm()
            tvInfoPayTotal.text = item.salePrice.setNumberForm()
        }
    }

    companion object {
        private const val EXTRA_PRODUCT_ID = "EXTRA_PRODUCT_ID"

        @JvmStatic
        fun createIntent(
            context: Context,
            productId: Long,
        ): Intent =
            Intent(context, SellInfoActivity::class.java).apply {
                putExtra(EXTRA_PRODUCT_ID, productId)
            }
    }
}