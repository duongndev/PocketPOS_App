package com.duongnd.pocketposapp.feature.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ScannedItem(
    val barcode: String,
    val count: Int
)

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {
    
    private val _scannedItems = MutableStateFlow<List<ScannedItem>>(emptyList())
    val scannedItems: StateFlow<List<ScannedItem>> = _scannedItems.asStateFlow()

    fun searchProductByBarcode(barcode: String) {
        viewModelScope.launch {
            Timber.tag("ScanViewModel").d(barcode)
            _scannedItems.update { currentList ->
                val index = currentList.indexOfFirst { it.barcode == barcode }
                if (index != -1) {
                    // Nếu đã tồn tại, tăng số lượng và đưa lên đầu
                    val updatedItem = currentList[index].copy(count = currentList[index].count + 1)
                    val newList = currentList.toMutableList()
                    newList.removeAt(index)
                    newList.add(0, updatedItem)
                    newList
                } else {
                    // Nếu chưa có, thêm mới vào đầu danh sách
                    listOf(ScannedItem(barcode, 1)) + currentList
                }
            }
        }
    }

    fun clearBarcodes() {
        _scannedItems.value = emptyList()
    }
}
