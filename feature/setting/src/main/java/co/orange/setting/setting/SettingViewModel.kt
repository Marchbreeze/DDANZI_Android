package co.orange.setting.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.state.UiState
import co.orange.domain.entity.response.SettingInfoModel
import co.orange.domain.usecase.setting.GetUserDefaultInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
@Inject
constructor(
    private val getUserDefaultInfoUseCase: GetUserDefaultInfoUseCase
) : ViewModel() {
    private val _getSettingInfoState = MutableStateFlow<UiState<SettingInfoModel>>(UiState.Empty)
    val getSettingInfoState: StateFlow<UiState<SettingInfoModel>> = _getSettingInfoState

    init {
        getSettingInfoFromServer()
    }

    private fun getSettingInfoFromServer() {
        _getSettingInfoState.value = UiState.Loading
        viewModelScope.launch {
            getUserDefaultInfoUseCase()
                .onSuccess {
                    _getSettingInfoState.value = UiState.Success(it)
                }
                .onFailure {
                    _getSettingInfoState.value = UiState.Failure(it.message.toString())
                }
        }
    }
}
