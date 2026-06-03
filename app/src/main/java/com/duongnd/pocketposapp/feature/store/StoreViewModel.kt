package com.duongnd.pocketposapp.feature.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.data.remote.dto.store.StoreRequest
import com.duongnd.pocketposapp.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StoreState())
    val state: StateFlow<StoreState> = _state.asStateFlow()

    init {
        loadStoreInfo()
    }

    private fun loadStoreInfo() {
        val store = repository.getStoreLocal()
        store?.let {
            _state.update { currentState ->
                currentState.copy(
                    storeName = it.storeName,
                    address = it.address ?: "",
                    phone = it.phoneNumber ?: "",
                    description = it.description ?: "",
                    bankName = it.bankName ?: "",
                    bankAccountNumber = it.bankAccountNumber ?: "",
                    bankAccountName = it.bankAccountName ?: ""
                )
            }
        }
    }

    fun onStoreNameChange(value: String) {
        _state.update { it.copy(storeName = value) }
    }

    fun onAddressChange(value: String) {
        _state.update { it.copy(address = value) }
    }

    fun onPhoneChange(value: String) {
        _state.update { it.copy(phone = value) }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onBankNameChange(value: String) {
        _state.update { it.copy(bankName = value) }
    }

    fun onBankAccountNumberChange(value: String) {
        _state.update { it.copy(bankAccountNumber = value) }
    }

    fun onBankAccountNameChange(value: String) {
        _state.update { it.copy(bankAccountName = value) }
    }

    fun updateStoreInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            val request = StoreRequest(
                storeName = state.value.storeName,
                description = state.value.description,
                phoneNumber = state.value.phone,
                address = state.value.address,
                logoUrl = null,
                bankName = state.value.bankName,
                bankAccountNumber = state.value.bankAccountNumber,
                bankAccountName = state.value.bankAccountName
            )
            val result = repository.updateStoreProfile(request)
            result.onSuccess {
                _state.update { currentState -> 
                    currentState.copy(
                        isLoading = false, 
                        isSuccess = true,
                        showResultDialog = true,
                        resultMessage = "Cập nhật thông tin cửa hàng thành công!"
                    ) 
                }
            }.onFailure { throwable ->
                _state.update { currentState -> 
                    currentState.copy(
                        isLoading = false, 
                        error = throwable.message ?: "Update failed",
                        showResultDialog = true,
                        isSuccess = false,
                        resultMessage = throwable.message ?: "Cập nhật thất bại, vui lòng thử lại."
                    ) 
                }
            }
        }
    }
    
    fun onDismissResultDialog() {
        _state.update { it.copy(showResultDialog = false) }
    }

    fun resetSuccess() {
        _state.update { it.copy(isSuccess = false) }
    }
}
