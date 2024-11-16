package co.orange.setting.delivery.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.domain.usecase.address.AddOrModUserAddressUseCase
import co.orange.domain.repository.UserRepository
import co.orange.domain.usecase.setting.GetUserDefaultInfoUseCase
import co.orange.setting.delivery.address.AddressActivity.Companion.DEFAULT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel
@Inject
constructor(
    private val getUserDefaultInfoUseCase: GetUserDefaultInfoUseCase,
    private val addOrModUserAddressUseCase: AddOrModUserAddressUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    var zipCode = ""
    var address = ""
    var detailAddress = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var phone = MutableLiveData<String>()

    var addressId: Long = DEFAULT_ID

    val isCompleted = MutableLiveData(false)

    private val _setAddressResult = MutableSharedFlow<Boolean>()
    val setAddressResult: SharedFlow<Boolean> = _setAddressResult

    init {
        getUserName()
        getUserPhone()
        getUserInfoFromServer()
    }

    private fun getUserName() {
        name.value = userRepository.getUserName()
    }

    private fun getUserPhone() {
        phone.value = userRepository.getUserPhone()
    }

    fun checkIsCompleted() {
        isCompleted.value =
            (
                    zipCode.isNotEmpty() &&
                            address.isNotEmpty() &&
                            detailAddress.value?.isNotEmpty() == true &&
                            name.value?.isNotEmpty() == true &&
                            phone.value?.length == 11
                    )
    }

    private fun getUserInfoFromServer() {
        viewModelScope.launch {
            getUserDefaultInfoUseCase()
                .onSuccess {
                    name.value = it.name
                    phone.value = it.phone
                }
        }
    }

    fun setUserAddressToServer() {
        viewModelScope.launch {
            addOrModUserAddressUseCase(
                addressId,
                name.value.orEmpty(),
                zipCode,
                address,
                detailAddress.value.orEmpty(),
                phone.value.orEmpty(),
            ).onSuccess {
                _setAddressResult.emit(true)
            }.onFailure {
                _setAddressResult.emit(false)
            }
        }
    }
}
