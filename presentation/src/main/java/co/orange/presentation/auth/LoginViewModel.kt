package co.orange.presentation.auth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        // private val authRepository: AuthRepository,
    ) : ViewModel()
