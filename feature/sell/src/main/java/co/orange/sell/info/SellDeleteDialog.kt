package co.orange.sell.info

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.orange.core.base.BaseDialog
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.stringOf
import co.orange.core.extension.toast
import co.orange.core.navigation.NavigationManager
import co.orange.core.state.UiState
import co.orange.sell.R
import co.orange.sell.databinding.DialogBankBinding
import co.orange.sell.databinding.DialogSellDeleteBinding
import co.orange.sell.progress.SellProgressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SellDeleteDialog :
    BaseDialog<DialogSellDeleteBinding>(R.layout.dialog_sell_delete) {

    private val viewModel by activityViewModels<SellInfoViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(co.orange.core.R.color.transparent)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initDeleteBtnListener()
        observeDeleteItemState()
    }

    private fun initDeleteBtnListener() {
        binding.btnSellDelete.setOnSingleClickListener {
            viewModel.deleteSellingItemFromServer()
        }
    }

    private fun observeDeleteItemState() {
        viewModel.deleteItemState
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        toast(stringOf(co.orange.core.R.string.sell_delete_success_toast))
                        requireActivity().finish()
                        dismiss()
                    }

                    is UiState.Failure -> toast(stringOf(co.orange.core.R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }
}
