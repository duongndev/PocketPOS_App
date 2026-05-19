package com.duongnd.pocketposapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.pocketposapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }
            authRepository.login(loginRequest)
                .onSuccess {
                    _loginState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _loginState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, error = null) }
            authRepository.register(registerRequest)
                .onSuccess {
                    _registerState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _registerState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun resetLoginState() {
        _loginState.update { LoginState() }
    }

    fun resetRegisterState() {
        _registerState.update { RegisterState() }
    }
}
