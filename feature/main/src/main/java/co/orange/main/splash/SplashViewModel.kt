package co.orange.main.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.domain.usecase.network.GetServerAvailableStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    private val getServerAvailableStatusUseCase: GetServerAvailableStatusUseCase
) : ViewModel() {
    private val _isServerAvailable = MutableSharedFlow<Boolean>()
    val isServerAvailable: SharedFlow<Boolean> = _isServerAvailable

    fun getServerStatusToCheckAvailable() {
        viewModelScope.launch {
            getServerAvailableStatusUseCase()
                .onSuccess {
                    _isServerAvailable.emit(true)
                }
                .onFailure {
                    _isServerAvailable.emit(false)
                }
        }
    }
}
