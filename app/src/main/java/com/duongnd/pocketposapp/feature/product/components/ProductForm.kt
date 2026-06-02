package com.duongnd.pocketposapp.feature.product.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.feature.product.AddEditProductState
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScannerView
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScanningOverlay

@Composable
fun ProductFormContent(
    state: AddEditProductState,
    onNameChange: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onBrandChange: (String) -> Unit,
    onCostPriceChange: (String) -> Unit,
    onSellingPriceChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onImageChange: (String?) -> Unit,
    onCreateCategory: (String) -> Unit
) {
    var showCategorySheet by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onImageChange(uri?.toString()) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanner = true
        }
    }

    if (showScanner) {
        BarcodeScannerDialog(
            onDismiss = { showScanner = false },
            onBarcodeScanned = { barcode ->
                onBarcodeChange(barcode)
                showScanner = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Image Picker
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                        .clickable { imageLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.imageUri != null) {
                        AsyncImage(
                            model = state.imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            Text("Thêm ảnh", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // Basic Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Thông tin chung", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    AppOutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        label = { Text("Tên sản phẩm *") }
                    )

                    CategorySelectionField(
                        selectedCategoryId = state.selectedCategoryId,
                        categories = state.categories,
                        onClick = { showCategorySheet = true },
                        onCategorySelect = onCategorySelect
                    )

                    AppOutlinedTextField(
                        value = state.brand,
                        onValueChange = onBrandChange,
                        label = { Text("Thương hiệu") }
                    )
                }
            }
        }

        // Pricing & Inventory
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Giá & Kho hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppOutlinedTextField(
                            value = state.costPrice,
                            onValueChange = onCostPriceChange,
                            label = { Text("Giá vốn") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        AppOutlinedTextField(
                            value = state.sellingPrice,
                            onValueChange = onSellingPriceChange,
                            label = { Text("Giá bán *") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppOutlinedTextField(
                            value = state.stock,
                            onValueChange = onStockChange,
                            label = { Text("Tồn kho") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        AppOutlinedTextField(
                            value = state.unit,
                            onValueChange = onUnitChange,
                            label = { Text("Đơn vị") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AppOutlinedTextField(
                        value = state.barcode,
                        onValueChange = onBarcodeChange,
                        label = { Text("Mã vạch / Barcode") },
                        trailingIcon = { 
                            IconButton(onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    showScanner = true
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )
                }
            }
        }

        // Description
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Mô tả", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    AppOutlinedTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Mô tả sản phẩm") },
                        singleLine = false,
                        minLines = 3,
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, null) }
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }

    if (showCategorySheet) {
        CategorySelectionSheet(
            categories = state.categories,
            selectedCategoryId = state.selectedCategoryId,
            onDismiss = { showCategorySheet = false },
            onCategorySelect = { id ->
                onCategorySelect(id)
                showCategorySheet = false
            },
            onCreateCategory = onCreateCategory
        )
    }
}

@Composable
fun CategorySelectionField(
    selectedCategoryId: String?,
    categories: List<Category>,
    onClick: () -> Unit,
    onCategorySelect: (String) -> Unit
) {
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Category, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = selectedCategory?.name ?: "Chọn danh mục *",
                style = MaterialTheme.typography.bodyLarge,
                color = if (selectedCategory != null) Color.Black else Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionSheet(
    categories: List<Category>,
    selectedCategoryId: String?,
    onDismiss: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onCreateCategory: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Danh mục", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddCategoryDialog = true }) {
                    Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                items(categories.size) { index ->
                    val category = categories[index]
                    val isSelected = category.id == selectedCategoryId
                    Surface(
                        onClick = { onCategorySelect(category.id) },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(category.name, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Thêm danh mục") },
            text = { 
                AppOutlinedTextField(value = newCategoryName, onValueChange = { newCategoryName = it }, label = { Text("Tên danh mục") })
            },
            confirmButton = { 
                Button(onClick = { 
                    if (newCategoryName.isNotBlank()) {
                        onCreateCategory(newCategoryName)
                        showAddCategoryDialog = false
                    }
                }) { Text("Lưu") }
            },
            dismissButton = { TextButton(onClick = { showAddCategoryDialog = false }) { Text("Hủy") } }
        )
    }
}

@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onBarcodeScanned: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            BarcodeScannerView(
                modifier = Modifier.fillMaxSize(),
                onBarcodeScanned = onBarcodeScanned
            )
            BarcodeScanningOverlay(modifier = Modifier.fillMaxSize())

            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Text(
                text = "Quét mã vạch sản phẩm",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
