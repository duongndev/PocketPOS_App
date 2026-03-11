package com.duongnd.pocketposapp.feature.product

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.domain.model.ProductVariant
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScannerView
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScanningOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    productId: Int = -1,
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var expandedCategory by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scanningVariantIndex by remember { mutableStateOf<Int?>(null) }
    
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                showScanner = true
            }
        }
    )

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            navController.popBackStack()
        }
    }

    if (showScanner) {
        BarcodeScannerDialog(
            onDismiss = { 
                showScanner = false
                scanningVariantIndex = null
            },
            onBarcodeScanned = { barcode ->
                scanningVariantIndex?.let { index ->
                    viewModel.updateVariantBarcode(index, barcode)
                }
                showScanner = false
                scanningVariantIndex = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == -1) "Thêm sản phẩm" else "Sửa sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveProduct() }) {
                        Text("LƯU", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Thông tin cơ bản
                item {
                    Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    AppOutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Tên sản phẩm *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        AppOutlinedTextField(
                            value = state.categories.find { it.id == state.selectedCategoryId }?.name ?: "Chọn danh mục",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Danh mục *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            state.categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        viewModel.onCategorySelect(category.id)
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.hasVariants,
                            onCheckedChange = { viewModel.onHasVariantsChange(it) }
                        )
                        Text("Sản phẩm có nhiều biến thể")
                    }
                }

                // 2. Phần thuộc tính (nếu có biến thể)
                if (state.hasVariants) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Thuộc tính", fontWeight = FontWeight.Bold)
                            TextButton(onClick = { viewModel.addAttribute() }) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Thêm")
                            }
                        }
                    }

                    itemsIndexed(state.attributes) { index, attr ->
                        AttributeItem(
                            attr = attr,
                            onNameChange = { viewModel.updateAttributeName(index, it) },
                            onAddValue = { viewModel.addAttributeValue(index, it) }
                        )
                    }
                }

                // 3. Danh sách biến thể / Thông tin giá & kho
                item {
                    Text(
                        if (state.hasVariants) "Danh sách biến thể" else "Thông tin bán hàng",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                itemsIndexed(state.variants) { index, variant ->
                    VariantInputItem(
                        variant = variant,
                        onUpdate = { p, cp, st, sku, bc ->
                            viewModel.updateVariant(index, p, cp, st, sku, bc)
                        },
                        onScanBarcode = {
                            scanningVariantIndex = index
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                showScanner = true
                            } else {
                                launcher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BarcodeScannerDialog(
    onDismiss: () -> Unit,
    onBarcodeScanned: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            BarcodeScannerView(
                modifier = Modifier.fillMaxSize(),
                onBarcodeScanned = onBarcodeScanned
            )
            BarcodeScanningOverlay(modifier = Modifier.fillMaxSize())
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Text(
                "Đưa mã vạch vào khung quét",
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp)
            )
        }
    }
}

@Composable
fun AttributeItem(
    attr: AttributeInput,
    onNameChange: (String) -> Unit,
    onAddValue: (String) -> Unit
) {
    var newValue by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AppOutlinedTextField(
                value = attr.name,
                onValueChange = onNameChange,
                label = { Text("Tên thuộc tính (VD: Màu sắc)") },
                modifier = Modifier.fillMaxWidth(),
                small = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attr.values.forEach { value ->
                    SuggestionChip(onClick = {}, label = { Text(value) })
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppOutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    label = { Text("Giá trị (VD: Đỏ)") },
                    modifier = Modifier.weight(1f),
                    small = true
                )
                IconButton(onClick = {
                    onAddValue(newValue)
                    newValue = ""
                }) {
                    Icon(Icons.Default.AddCircle, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun VariantInputItem(
    variant: ProductVariant,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    var price by remember { mutableStateOf(if (variant.price == 0.0) "" else variant.price.toString()) }
    var stock by remember { mutableStateOf(if (variant.stock == 0) "" else variant.stock.toString()) }
    var sku by remember { mutableStateOf(variant.sku ?: "") }
    var barcode by remember { mutableStateOf(variant.barcode ?: "") }

    LaunchedEffect(variant.barcode) {
        barcode = variant.barcode ?: ""
    }

    val displayName = if (variant.attributes.isEmpty()) "Mặc định" 
                      else variant.attributes.joinToString(" - ") { it.value }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(displayName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppOutlinedTextField(
                    value = price,
                    onValueChange = { 
                        price = it
                        onUpdate(it.toDoubleOrNull() ?: 0.0, variant.costPrice, stock.toIntOrNull() ?: 0, sku, barcode)
                    },
                    label = { Text("Giá nhập") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AppOutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        onUpdate(it.toDoubleOrNull() ?: 0.0, variant.price, stock.toIntOrNull() ?: 0, sku, barcode)
                    },
                    label = { Text("Giá bán") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

            }

            AppOutlinedTextField(
                value = stock,
                onValueChange = {
                    stock = it
                    onUpdate(price.toDoubleOrNull() ?: 0.0, variant.costPrice, it.toIntOrNull() ?: 0, sku, barcode)
                },
                label = { Text("Tồn kho") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            AppOutlinedTextField(
                value = barcode,
                onValueChange = { 
                    barcode = it
                    onUpdate(price.toDoubleOrNull() ?: 0.0, variant.costPrice, stock.toIntOrNull() ?: 0, sku, it)
                },
                label = { Text("Mã vạch / Barcode") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                trailingIcon = {
                    IconButton(onClick = onScanBarcode) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan")
                    }
                }
            )
            
            AppOutlinedTextField(
                value = sku,
                onValueChange = { 
                    sku = it
                    onUpdate(price.toDoubleOrNull() ?: 0.0, variant.costPrice, stock.toIntOrNull() ?: 0, it, barcode)
                },
                label = { Text("Mã SKU (tùy chọn)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    small: Boolean = false,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        readOnly = readOnly,
        textStyle = if (small) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        shape = shape,
        singleLine = true,
        trailingIcon = trailingIcon
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}
