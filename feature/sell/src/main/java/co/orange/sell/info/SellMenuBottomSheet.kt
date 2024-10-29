package co.orange.sell.info

import android.os.Bundle
import android.view.View
import co.orange.core.base.BaseBottomSheet
import co.orange.core.extension.setOnSingleClickListener
import co.orange.sell.R
import co.orange.sell.databinding.BottomSheetSellMenuBinding

class SellMenuBottomSheet :
    BaseBottomSheet<BottomSheetSellMenuBinding>(R.layout.bottom_sheet_sell_menu) {

    private var sellDeleteDialog: SellDeleteDialog? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(co.orange.core.R.color.transparent)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initSubmitBtnListener()
    }

    private fun initSubmitBtnListener() {
        binding.btnDelete.setOnSingleClickListener {
            sellDeleteDialog = SellDeleteDialog()
            sellDeleteDialog?.show(childFragmentManager, sellDeleteDialog?.tag)
            dismiss()
        }
    }
}
