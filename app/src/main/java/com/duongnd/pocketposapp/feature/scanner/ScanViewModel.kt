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
    val name: String,
    val price: Double,
    val count: Int
)

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {
    
    private val _scannedItems = MutableStateFlow<List<ScannedItem>>(listOf(
        ScannedItem("8934567890123", "Coca Cola 330ml", 10000.0, 2),
        ScannedItem("8935217400107", "Bánh mì Kinh Đô", 15000.0, 1),
        ScannedItem("6901234567890", "Nước suối Aquafina 500ml", 5000.0, 3)
    ))
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
                    // Nếu chưa có, giả lập tìm thấy sản phẩm mới
                    val newItem = when(barcode) {
                        "8934567890123" -> ScannedItem(barcode, "Coca Cola 330ml", 10000.0, 1)
                        "8935217400107" -> ScannedItem(barcode, "Bánh mì Kinh Đô", 15000.0, 1)
                        else -> ScannedItem(barcode, "Sản phẩm mới", (10..50).random() * 1000.0, 1)
                    }
                    listOf(newItem) + currentList
                }
            }
        }
    }

    fun clearBarcodes() {
        _scannedItems.value = emptyList()
    }

    fun increaseCount(barcode: String) {
        _scannedItems.update { currentList ->
            currentList.map {
                if (it.barcode == barcode) it.copy(count = it.count + 1) else it
            }
        }
    }

    fun decreaseCount(barcode: String) {
        _scannedItems.update { currentList ->
            currentList.mapNotNull {
                if (it.barcode == barcode) {
                    if (it.count > 1) it.copy(count = it.count - 1) else null
                } else it
            }
        }
    }
}
