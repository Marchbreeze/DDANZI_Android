package co.orange.setting.bank.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.domain.repository.SettingRepository
import co.orange.domain.usecase.bank.AddOrModUserBankUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankAddViewModel
@Inject
constructor(
    private val addOrModUserBankUseCase: AddOrModUserBankUseCase
) : ViewModel() {
    var accountId: Long = -1

    var ownerName = ""
    var maskedName = MutableLiveData<String>()

    var bankName = MutableLiveData<String>()
    var bankCode = ""
    var accountNumber = MutableLiveData<String>()

    val isBankSelected = MutableLiveData(false)
    val isCompleted = MutableLiveData(false)

    private val _setBankResult = MutableSharedFlow<Boolean>()
    val setBankResult: SharedFlow<Boolean> = _setBankResult

    fun checkIsCompleted() {
        isCompleted.value =
            (!maskedName.value.isNullOrEmpty() && bankCode.isNotEmpty() && !accountNumber.value.isNullOrEmpty())
    }

    fun setBankToServer() {
        viewModelScope.launch {
            addOrModUserBankUseCase(accountId, ownerName, bankCode, accountNumber.value.orEmpty())
                .onSuccess {
                    _setBankResult.emit(true)
                }.onFailure {
                    _setBankResult.emit(false)
                }
        }
    }
}
