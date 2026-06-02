package com.duongnd.pocketposapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.pocketposapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiEvent {
    data class ShowToast(val message: String) : AuthUiEvent()
    object LoginSuccess : AuthUiEvent()
    object RegisterSuccess : AuthUiEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AuthUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEmailChanged(email: String) {
        _loginState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _loginState.update { it.copy(password = password, passwordError = null) }
    }

    // Register handlers
    fun onRegisterStoreNameChanged(value: String) {
        _registerState.update { it.copy(storeName = value, storeNameError = null) }
    }

    fun onRegisterFullNameChanged(value: String) {
        _registerState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onRegisterEmailChanged(value: String) {
        _registerState.update { it.copy(email = value, emailError = null) }
    }

    fun onRegisterPhoneChanged(value: String) {
        _registerState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onRegisterPasswordChanged(value: String) {
        _registerState.update { it.copy(password = value, passwordError = null) }
    }

    fun onRegisterConfirmPasswordChanged(value: String) {
        _registerState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun login() {
        val email = _loginState.value.email
        val password = _loginState.value.password

        if (email.isBlank()) {
            _loginState.update { it.copy(emailError = "Email không được để trống") }
            return
        }
        if (password.isBlank()) {
            _loginState.update { it.copy(passwordError = "Mật khẩu không được để trống") }
            return
        }

        viewModelScope.launch {
            _loginState.update { it.copy(isLoading = true, error = null) }
            authRepository.login(LoginRequest(email, password))
                .onSuccess {
                    _loginState.update { it.copy(isLoading = false, isSuccess = true) }
                    _eventFlow.emit(AuthUiEvent.LoginSuccess)
                }
                .onFailure { e ->
                    _loginState.update { it.copy(isLoading = false, error = e.message) }
                    _eventFlow.emit(AuthUiEvent.ShowToast(e.message ?: "Đăng nhập thất bại"))
                }
        }
    }

    fun register() {
        val state = _registerState.value
        
        var hasError = false
        if (state.storeName.isBlank()) {
            _registerState.update { it.copy(storeNameError = "Tên cửa hàng không được để trống") }
            hasError = true
        }
        if (state.fullName.isBlank()) {
            _registerState.update { it.copy(fullNameError = "Họ tên không được để trống") }
            hasError = true
        }
        if (state.email.isBlank()) {
            _registerState.update { it.copy(emailError = "Email không được để trống") }
            hasError = true
        }
        if (state.phone.isBlank()) {
            _registerState.update { it.copy(phoneError = "Số điện thoại không được để trống") }
            hasError = true
        }
        if (state.password.isBlank()) {
            _registerState.update { it.copy(passwordError = "Mật khẩu không được để trống") }
            hasError = true
        }
        if (state.confirmPassword != state.password) {
            _registerState.update { it.copy(confirmPasswordError = "Mật khẩu xác nhận không khớp") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _registerState.update { it.copy(isLoading = true, error = null) }
            val request = RegisterRequest(
                storeName = state.storeName,
                fullName = state.fullName,
                email = state.email,
                phone = state.phone,
                password = state.password,
                confirmPassword = state.confirmPassword
            )
            authRepository.register(request)
                .onSuccess {
                    _registerState.update { it.copy(isLoading = false, isSuccess = true) }
                    _eventFlow.emit(AuthUiEvent.RegisterSuccess)
                }
                .onFailure { e ->
                    _registerState.update { it.copy(isLoading = false, error = e.message) }
                    _eventFlow.emit(AuthUiEvent.ShowToast(e.message ?: "Đăng ký thất bại"))
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
