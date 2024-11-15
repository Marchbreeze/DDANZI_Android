package co.orange.sell.onboarding

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.orange.core.extension.getFileName
import co.orange.core.state.UiState
import co.orange.domain.usecase.upload.UploadGetOCRResultUseCase
import co.orange.domain.usecase.upload.UploadGetSignedUrlUseCase
import co.orange.domain.usecase.upload.UploadPutImageToCloudUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellOnboardingViewModel
@Inject
constructor(
    private val uploadGetSignedUrlUseCase: UploadGetSignedUrlUseCase,
    private val uploadPutImageToCloudUseCase: UploadPutImageToCloudUseCase,
    private val uploadGetOCRResultUseCase: UploadGetOCRResultUseCase
) : ViewModel() {
    private var selectedImageUri = ""
    private var selectedImageName = ""
    var uploadedUrl = ""

    var productId = ""
    var productName = ""
    var productImage = ""

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
        selectedImageUri = uri.toString()
        selectedImageName = uri.getFileName(contentResolver).orEmpty()
        _changingImageState.value = UiState.Loading
        viewModelScope.launch {
            uploadGetSignedUrlUseCase(selectedImageName)
                .onSuccess {
                    uploadedUrl = URL_GCP + selectedImageName
                    putImageToCloud(it.signedUrl)
                }
                .onFailure {
                    _changingImageState.value = UiState.Failure(it.message.toString())
                    resetChangeImageState()
                }
        }
    }

    private fun putImageToCloud(url: String) {
        viewModelScope.launch {
            uploadPutImageToCloudUseCase(url, selectedImageUri)
                .onSuccess {
                    postToCheckProduct()
                }.onFailure {
                    _changingImageState.value = UiState.Failure(it.message.toString())
                    resetChangeImageState()
                }
        }
    }

    private fun postToCheckProduct() {
        viewModelScope.launch {
            uploadGetOCRResultUseCase(uploadedUrl)
                .onSuccess {
                    productId = it.productId
                    productName = it.productName
                    productImage = it.imgUrl
                    _changingImageState.value = UiState.Success(it.productId)
                }
                .onFailure {
                    _changingImageState.value = UiState.Failure(it.message.toString())
                    resetChangeImageState()
                }
        }
    }

    fun resetChangeImageState() {
        _changingImageState.value = UiState.Empty
    }

    companion object {
        private const val URL_GCP = "https://storage.googleapis.com/ddanzi_bucket/"
    }
}
