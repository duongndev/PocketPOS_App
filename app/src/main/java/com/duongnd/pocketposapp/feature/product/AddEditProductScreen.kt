package com.duongnd.pocketposapp.feature.product

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
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
    productId: String = "-1",
    viewModel: AddEditProductViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var expandedCategory by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scanningVariantIndex by remember { mutableStateOf<Int?>(null) }
    
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> if (granted) showScanner = true }
    )

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) navController.popBackStack()
    }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    if (showScanner) {
        BarcodeScannerDialog(
            onDismiss = { showScanner = false; scanningVariantIndex = null },
            onBarcodeScanned = { barcode ->
                scanningVariantIndex?.let { viewModel.updateVariantBarcode(it, barcode) }
                showScanner = false
                scanningVariantIndex = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (productId == "-1") "Thêm sản phẩm" else "Sửa sản phẩm",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.saveProduct() },
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("LƯU", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Thông tin cơ bản
            item {
                SectionHeader(title = "Thông tin chung", icon = Icons.Default.Info)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppOutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            label = { Text("Tên sản phẩm *") }
                        )

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
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
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

                        AppOutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.onDescriptionChange(it) },
                            label = { Text("Mô tả sản phẩm (tùy chọn)") },
                            singleLine = false,
                            minLines = 2
                        )
                    }
                }
            }

            // 2. Chế độ biến thể Toggle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onHasVariantsChange(!state.hasVariants) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sản phẩm có nhiều biến thể", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = state.hasVariants,
                            onCheckedChange = { viewModel.onHasVariantsChange(it) }
                        )
                    }
                }
            }

            // 3. Phần Thuộc tính & Biến thể (Chỉ hiện khi BẬT biến thể)
            if (state.hasVariants) {
                item {
                    SectionHeader(
                        title = "Thiết lập thuộc tính",
                        icon = Icons.Default.Tune,
                        action = {
                            TextButton(onClick = { viewModel.addAttribute() }) {
                                Icon(Icons.Default.Add, null, Modifier.size(18.dp))
                                Text("Thêm thuộc tính")
                            }
                        }
                    )
                }

                itemsIndexed(state.attributes) { index, attr ->
                    AttributeItem(
                        attr = attr,
                        onNameChange = { viewModel.updateAttributeName(index, it) },
                        onAddValue = { viewModel.addAttributeValue(index, it) },
                        onDelete = { viewModel.removeAttribute(index) },
                        onDeleteValue = { viewModel.removeAttributeValue(index, it) }
                    )
                }

                if (state.variants.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Danh sách biến thể", icon = Icons.Default.FormatListBulleted)
                    }
                    itemsIndexed(state.variants) { index, variant ->
                        VariantInputItem(
                            variant = variant,
                            onUpdate = { p, cp, st, sku, bc -> viewModel.updateVariant(index, p, cp, st, sku, bc) },
                            onScanBarcode = {
                                scanningVariantIndex = index
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) showScanner = true
                                else cameraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        )
                    }
                }
            } else {
                // 4. Giá & Tồn kho cho sản phẩm đơn giản (Khi TẮT biến thể)
                item {
                    SectionHeader(title = "Giá bán & Tồn kho", icon = Icons.Default.Payments)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.variants.isNotEmpty()) {
                        SimplePricingCard(
                            variant = state.variants.first(),
                            onUpdate = { p, cp, st, sku, bc -> viewModel.updateVariant(0, p, cp, st, sku, bc) },
                            onScanBarcode = {
                                scanningVariantIndex = 0
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) showScanner = true
                                else cameraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun SimplePricingCard(
    variant: ProductVariant,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppOutlinedTextField(
                    value = if (variant.costPrice == 0.0) "" else variant.costPrice.toString(),
                    onValueChange = { onUpdate(variant.price, it.toDoubleOrNull() ?: 0.0, variant.stock, variant.sku, variant.barcode) },
                    label = { Text("Giá nhập") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AppOutlinedTextField(
                    value = if (variant.price == 0.0) "" else variant.price.toString(),
                    onValueChange = { onUpdate(it.toDoubleOrNull() ?: 0.0, variant.costPrice, variant.stock, variant.sku, variant.barcode) },
                    label = { Text("Giá bán") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppOutlinedTextField(
                    value = if (variant.stock == 0) "" else variant.stock.toString(),
                    onValueChange = { onUpdate(variant.price, variant.costPrice, it.toIntOrNull() ?: 0, variant.sku, variant.barcode) },
                    label = { Text("Số lượng tồn") },
                    modifier = Modifier.weight(0.4f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AppOutlinedTextField(
                    value = variant.sku ?: "",
                    onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, it, variant.barcode) },
                    label = { Text("Mã SKU") },
                    modifier = Modifier.weight(0.6f)
                )
            }
            AppOutlinedTextField(
                value = variant.barcode ?: "",
                onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, variant.sku, it) },
                label = { Text("Mã vạch / Barcode") },
                trailingIcon = {
                    IconButton(onClick = onScanBarcode) {
                        Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    }
}

@Composable
fun VariantInputItem(
    variant: ProductVariant,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    val displayName = variant.attributes.joinToString(" - ") { it.value }
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(displayName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AppOutlinedTextField(
                    value = if (variant.costPrice == 0.0) "" else variant.costPrice.toString(),
                    onValueChange = { onUpdate(variant.price, it.toDoubleOrNull() ?: 0.0, variant.stock, variant.sku, variant.barcode) },
                    label = { Text("Giá nhập") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AppOutlinedTextField(
                    value = if (variant.price == 0.0) "" else variant.price.toString(),
                    onValueChange = { onUpdate(it.toDoubleOrNull() ?: 0.0, variant.costPrice, variant.stock, variant.sku, variant.barcode) },
                    label = { Text("Giá bán") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            AppOutlinedTextField(
                value = variant.barcode ?: "",
                onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, variant.sku, it) },
                label = { Text("Barcode") },
                trailingIcon = { IconButton(onClick = onScanBarcode) { Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary) } }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector, action: @Composable (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        action?.invoke()
    }
}

@Composable
fun AttributeItem(
    attr: AttributeInput,
    onNameChange: (String) -> Unit,
    onAddValue: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteValue: (Int) -> Unit
) {
    var newValue by remember { mutableStateOf("") }
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppOutlinedTextField(
                    attr.name,
                    onNameChange,
                    label = { Text("Tên thuộc tính (VD: Màu sắc)") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete attribute", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            FlowRowCu(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                attr.values.forEachIndexed { vIndex, value ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(value) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                null,
                                Modifier.size(14.dp).clickable { onDeleteValue(vIndex) }
                            )
                        }
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppOutlinedTextField(newValue, { newValue = it }, label = { Text("Giá trị (VD: Đỏ)") }, modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { if (newValue.isNotBlank()) { onAddValue(newValue); newValue = "" } },
                    modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun BarcodeScannerDialog(onDismiss: () -> Unit, onBarcodeScanned: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            BarcodeScannerView(modifier = Modifier.fillMaxSize(), onBarcodeScanned = onBarcodeScanned)
            BarcodeScanningOverlay(modifier = Modifier.fillMaxSize())
            IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) { Icon(Icons.Default.Close, null, tint = Color.White) }
            Text("Đưa mã vạch vào khung quét", color = Color.White, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp))
        }
    }
}

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        minLines = minLines,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowCu(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable FlowRowScope.() -> Unit
) {
   FlowRow(modifier = modifier, horizontalArrangement = horizontalArrangement, content = content)
}
