package com.duongnd.pocketposapp.feature.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    data object Idle : SplashUiState()
    data object Authenticated : SplashUiState()
    data object Unauthenticated : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharedPrefs: ShareReferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun checkAuth() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            
            try {
                val token = sharedPrefs.getAccessToken()
                Log.d("SplashViewModel", "Checking auth, token exists: ${!token.isNullOrEmpty()}")
                
                if (token.isNullOrEmpty()) {
                    ensureMinDelay(startTime)
                    _uiState.value = SplashUiState.Unauthenticated
                    return@launch
                }

                val result = authRepository.getMe()
                Log.d("SplashViewModel", "getMe result: $result")
                ensureMinDelay(startTime)

                if (result.isSuccess) {
                    _uiState.value = SplashUiState.Authenticated
                } else {
                    _uiState.value = SplashUiState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("SplashViewModel", "Error in checkAuth", e)
                ensureMinDelay(startTime)
                _uiState.value = SplashUiState.Unauthenticated
            }
        }
    }

    private suspend fun ensureMinDelay(startTime: Long) {
        val elapsedTime = System.currentTimeMillis() - startTime
        if (elapsedTime < 2000) {
            delay(2000 - elapsedTime)
        }
    }
}
