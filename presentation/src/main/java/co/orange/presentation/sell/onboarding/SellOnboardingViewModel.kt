package co.orange.presentation.sell.onboarding

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.extension.getFileName
import co.orange.core.state.UiState
import co.orange.domain.repository.SellRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SellOnboardingViewModel
    @Inject
    constructor(
        private val sellRepository: SellRepository,
    ) : ViewModel() {
        private var selectedImageId = ""
        private var selectedImageName = ""

        private val _isCheckedAgain = MutableSharedFlow<Boolean>()
        val isCheckedAgain: SharedFlow<Boolean> = _isCheckedAgain

        private val _changingImageState = MutableStateFlow<UiState<String>>(UiState.Empty)
        val changingImageState: StateFlow<UiState<String>> = _changingImageState

        fun setCheckedAgain(state: Boolean) {
            viewModelScope.launch {
                _isCheckedAgain.emit(state)
            }
        }

        fun startSendingImage(
            uri: Uri,
            contentResolver: ContentResolver,
        ) {
            selectedImageId = uri.hashCode().toString()
            selectedImageName = uri.getFileName(contentResolver).orEmpty()
            _changingImageState.value = UiState.Loading
            viewModelScope.launch {
                sellRepository.getSignedUrl(selectedImageName)
                    .onSuccess {
                        // 바로 이미지 보내기
                        Timber.tag("okhttp").d("@@@@@@@@@@ ${it.signedUrl}")
                    }
                    .onFailure {
                        _changingImageState.value = UiState.Failure(it.message.toString())
                    }
            }
        }

        fun resetProductIdState() {
            _changingImageState.value = UiState.Empty
        }
    }
