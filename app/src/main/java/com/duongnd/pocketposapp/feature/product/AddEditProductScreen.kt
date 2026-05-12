package com.duongnd.pocketposapp.feature.product

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
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
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.onImageChange(uri?.toString()) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) showScanner = true }

    // Navigation and Side Effects
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) navController.popBackStack()
    }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    BackHandler {
        showDiscardDialog = true
    }

    // Dialogs
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Hủy thay đổi?") },
            text = { Text("Tất cả các thay đổi chưa lưu sẽ bị mất.") },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) { Text("ĐỒNG Ý", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("TIẾP TỤC SỬA") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa sản phẩm?") },
            text = { Text("Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteProduct(); showDeleteDialog = false }) { Text("XÓA", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("HỦY") }
            }
        )
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
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (productId == "-1") "Thêm sản phẩm mới" else "Chỉnh sửa sản phẩm",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (productId != "-1") {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    TextButton(
                        onClick = { viewModel.saveProduct() },
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("LƯU", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Hình ảnh sản phẩm
            item {
                ProductImageHeader(
                    uri = state.imageUri,
                    onClick = { imageLauncher.launch("image/*") }
                )
            }

            // 2. Thông tin cơ bản
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Thông tin cơ bản", icon = Icons.Default.Inventory)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AppOutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("Tên sản phẩm *") },
                        isError = state.name.isBlank() && state.error != null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CategoryDropdown(
                        selectedCategoryId = state.selectedCategoryId,
                        categories = state.categories,
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = it },
                        onCategorySelect = { viewModel.onCategorySelect(it); expandedCategory = false },
                        isError = state.selectedCategoryId == null && state.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppOutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.onDescriptionChange(it) },
                        label = { Text("Mô tả") },
                        placeholder = { Text("Nhập mô tả sản phẩm...") },
                        singleLine = false,
                        minLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                }
            }

            item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant) }

            // 3. Biến thể Toggle
            item {
                VariantToggle(
                    hasVariants = state.hasVariants,
                    onCheckedChange = { viewModel.onHasVariantsChange(it) }
                )
            }

            if (state.hasVariants) {
                // Editor thuộc tính
                item {
                    AttributeHeader(onAddAttribute = { viewModel.addAttribute() })
                }

                itemsIndexed(state.attributes) { index, attr ->
                    AttributeItem(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        attr = attr,
                        onNameChange = { viewModel.updateAttributeName(index, it) },
                        onAddValue = { viewModel.addAttributeValue(index, it) },
                        onDelete = { viewModel.removeAttribute(index) },
                        onDeleteValue = { viewModel.removeAttributeValue(index, it) }
                    )
                }

                // Danh sách biến thể
                if (state.variants.isNotEmpty()) {
                    item {
                        VariantListHeader(
                            count = state.variants.size,
                            onSeeAll = { navController.navigate(Routes.PRODUCT_VARIANTS) }
                        )
                    }

                    itemsIndexed(state.variants.take(5)) { index, variant ->
                        VariantSummaryItem(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            variant = variant,
                            onUpdate = { p, cp, st, sku, bc -> viewModel.updateVariant(index, p, cp, st, sku, bc) },
                            onScanBarcode = {
                                scanningVariantIndex = index
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) showScanner = true
                                else cameraLauncher.launch(Manifest.permission.CAMERA)
                            }
                        )
                    }
                    
                    if (state.variants.size > 5) {
                        item {
                            Text(
                                "Còn ${state.variants.size - 5} biến thể khác...",
                                modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { navController.navigate(Routes.PRODUCT_VARIANTS) },
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                // Giá & Kho cho sản phẩm đơn lẻ
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader(title = "Giá & Tồn kho", icon = Icons.Default.Payments)
                        Spacer(modifier = Modifier.height(16.dp))
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
            }
        }
    }
}

@Composable
fun ProductImageHeader(uri: String?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "Product Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Thêm hình ảnh sản phẩm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategoryId: String?,
    categories: List<com.duongnd.pocketposapp.domain.model.Category>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCategorySelect: (String) -> Unit,
    isError: Boolean
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        AppOutlinedTextField(
            value = categories.find { it.id == selectedCategoryId }?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Danh mục *") },
            placeholder = { Text("Chọn danh mục") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = { onCategorySelect(category.id) }
                )
            }
        }
    }
}

@Composable
fun VariantToggle(hasVariants: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionHeader(title = "Biến thể sản phẩm", icon = Icons.Default.AccountTree)
            Switch(
                checked = hasVariants,
                onCheckedChange = onCheckedChange
            )
        }
        Text(
            "Bật nếu sản phẩm có nhiều kích thước, màu sắc...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AttributeHeader(onAddAttribute: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thuộc tính", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            TextButton(onClick = onAddAttribute) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Thêm thuộc tính")
            }
        }
    }
}

@Composable
fun VariantListHeader(count: Int, onSeeAll: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Danh sách biến thể ($count)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onSeeAll) {
                Text("Xem tất cả")
                Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AttributeItem(
    modifier: Modifier = Modifier,
    attr: AttributeInput,
    onNameChange: (String) -> Unit,
    onAddValue: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteValue: (Int) -> Unit
) {
    var newValue by remember { mutableStateOf("") }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = attr.name,
                    onValueChange = onNameChange,
                    placeholder = { Text("Tên thuộc tính (VD: Màu sắc)") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attr.values.forEachIndexed { index, value ->
                    InputChip(
                        selected = false,
                        onClick = { },
                        label = { Text(value) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp).clickable { onDeleteValue(index) }
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    placeholder = { Text("Giá trị (VD: Đỏ)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { if (newValue.isNotBlank()) { onAddValue(newValue); newValue = "" } },
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun VariantSummaryItem(
    modifier: Modifier = Modifier,
    variant: ProductVariant,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    val displayName = variant.attributes.joinToString(" - ") { it.value }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = if (variant.price == 0.0) "" else variant.price.toString(),
                    onValueChange = { onUpdate(it.toDoubleOrNull() ?: 0.0, variant.costPrice, variant.stock, variant.sku, variant.barcode) },
                    label = { Text("Giá bán (₫)", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodySmall,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = variant.stock.toString(),
                    onValueChange = { onUpdate(variant.price, variant.costPrice, it.toIntOrNull() ?: 0, variant.sku, variant.barcode) },
                    label = { Text("Tồn kho", fontSize = 10.sp) },
                    modifier = Modifier.weight(0.6f),
                    textStyle = MaterialTheme.typography.bodySmall,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}

@Composable
fun SimplePricingCard(
    variant: ProductVariant,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppOutlinedTextField(
                value = if (variant.costPrice == 0.0) "" else variant.costPrice.toString(),
                onValueChange = { onUpdate(variant.price, it.toDoubleOrNull() ?: 0.0, variant.stock, variant.sku, variant.barcode) },
                label = { Text("Giá nhập") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
            AppOutlinedTextField(
                value = if (variant.price == 0.0) "" else variant.price.toString(),
                onValueChange = { onUpdate(it.toDoubleOrNull() ?: 0.0, variant.costPrice, variant.stock, variant.sku, variant.barcode) },
                label = { Text("Giá bán") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppOutlinedTextField(
                value = variant.stock.toString(),
                onValueChange = { onUpdate(variant.price, variant.costPrice, it.toIntOrNull() ?: 0, variant.sku, variant.barcode) },
                label = { Text("Tồn kho") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )
            AppOutlinedTextField(
                value = variant.sku ?: "",
                onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, it, variant.barcode) },
                label = { Text("Mã SKU") },
                modifier = Modifier.weight(1.5f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
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
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
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
