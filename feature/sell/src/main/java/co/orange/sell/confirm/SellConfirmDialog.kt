package co.orange.sell.confirm

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import co.orange.core.R
import co.orange.core.amplitude.AmplitudeManager
import co.orange.core.base.BaseDialog
import co.orange.core.extension.setOnSingleClickListener
import co.orange.core.extension.stringOf
import co.orange.core.extension.toast
import co.orange.core.navigation.NavigationManager
import co.orange.core.state.UiState
import co.orange.sell.databinding.DialogSellConfirmBinding
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import co.orange.sell.R as featureR

class SellConfirmDialog :
    BaseDialog<DialogSellConfirmBinding>(featureR.layout.dialog_sell_confirm) {
    @Inject
    lateinit var navigationManager: NavigationManager

    private val viewModel by activityViewModels<SellConfirmViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initReturnBtnListener()
        initConfirmBtnListener()
        observePatchOrderConfirmState()
    }

    private fun initReturnBtnListener() {
        binding.btnReturn.setOnSingleClickListener { dismiss() }
    }

    private fun initConfirmBtnListener() {
        binding.btnConfirm.setOnSingleClickListener {
            viewModel.patchOrderConfirmToServer()
        }
    }

    private fun observePatchOrderConfirmState() {
        viewModel.patchOrderConfirmState.flowWithLifecycle(lifecycle).distinctUntilChanged()
            .onEach { state ->
                when (state) {
                    is UiState.Success -> {
                        AmplitudeManager.apply {
                            plusIntProperty("user_selling_count", 1)
                            plusIntProperty("user_selling_total", viewModel.totalPrice)
                        }
                        toast(stringOf(R.string.sell_order_fix_msg))
                        navigationManager.toMainViewWIthClearing(requireContext())
                    }

                    is UiState.Failure -> toast(stringOf(R.string.error_msg))
                    else -> return@onEach
                }
            }.launchIn(lifecycleScope)
    }
}
