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
import androidx.compose.ui.graphics.Brush
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
    val primaryColor = MaterialTheme.colorScheme.primary

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
        if (state.name.isNotBlank() || state.selectedCategoryId != null) {
            showDiscardDialog = true
        } else {
            navController.popBackStack()
        }
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
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (productId == "-1") "Thêm sản phẩm mới" else "Chỉnh sửa sản phẩm",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (state.name.isNotBlank()) showDiscardDialog = true 
                        else navController.popBackStack() 
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (productId != "-1") {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete")
                        }
                    }
                    TextButton(
                        onClick = { viewModel.saveProduct() },
                        enabled = !state.isLoading,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("LƯU", fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(primaryColor)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF8FAFC), // Ultra light blue-grey background
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp) // Subtle separation
                ) {
                    // 1. Image Header Section
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.White)
                                .padding(24.dp)
                        ) {
                            ProductImagePicker(
                                uri = state.imageUri,
                                onPickImage = { imageLauncher.launch("image/*") }
                            )
                        }
                    }

                    // 2. Basic Information
                    item {
                        FormSectionCard(title = "Thông tin sản phẩm", icon = Icons.Default.Info) {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                AppOutlinedTextField(
                                    value = state.name,
                                    onValueChange = { viewModel.onNameChange(it) },
                                    label = { Text("Tên sản phẩm *") },
                                    placeholder = { Text("Ví dụ: Cà phê sữa đá") },
                                    isError = state.name.isBlank() && state.error != null,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                CategoryDropdownField(
                                    selectedCategoryId = state.selectedCategoryId,
                                    categories = state.categories,
                                    expanded = expandedCategory,
                                    onExpandedChange = { expandedCategory = it },
                                    onCategorySelect = { viewModel.onCategorySelect(it) },
                                    isError = state.selectedCategoryId == null && state.error != null
                                )

                                AppOutlinedTextField(
                                    value = state.description ?: "",
                                    onValueChange = { viewModel.onDescriptionChange(it) },
                                    label = { Text("Mô tả") },
                                    placeholder = { Text("Thông tin chi tiết về sản phẩm...") },
                                    singleLine = false,
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // 3. Variant Management
                    item {
                        FormSectionCard(
                            title = "Phân loại hàng hóa",
                            icon = Icons.Default.Layers,
                            trailing = {
                                Switch(
                                    checked = state.hasVariants,
                                    onCheckedChange = { viewModel.onHasVariantsChange(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = primaryColor)
                                )
                            }
                        ) {
                            if (state.hasVariants) {
                                VariantManagerContent(
                                    attributes = state.attributes,
                                    variants = state.variants,
                                    onAddAttribute = { viewModel.addAttribute() },
                                    onUpdateAttributeName = { i, name -> viewModel.updateAttributeName(i, name) },
                                    onAddAttributeValue = { i, v -> viewModel.addAttributeValue(i, v) },
                                    onRemoveAttribute = { i -> viewModel.removeAttribute(i) },
                                    onRemoveAttributeValue = { i, vi -> viewModel.removeAttributeValue(i, vi) },
                                    onNavigateToVariants = { navController.navigate(Routes.PRODUCT_VARIANTS) }
                                )
                            } else {
                                Text(
                                    "Sản phẩm có nhiều kích thước, màu sắc hoặc thuộc tính khác nhau.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 4. Simple Pricing (if no variants)
                    if (!state.hasVariants && state.variants.isNotEmpty()) {
                        item {
                            FormSectionCard(title = "Giá & Kho hàng", icon = Icons.Default.LocalOffer) {
                                SimplePricingForm(
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

    // Confirmation Dialogs
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Hủy bỏ thay đổi?") },
            text = { Text("Tất cả thông tin bạn vừa nhập sẽ không được lưu lại.") },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("HỦY BỎ", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("TIẾP TỤC")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa sản phẩm?") },
            text = { Text("Bạn có chắc chắn muốn xóa sản phẩm này? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteProduct(); showDeleteDialog = false }) {
                    Text("XÓA NGAY", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("HỦY")
                }
            }
        )
    }
}

@Composable
fun FormSectionCard(
    title: String,
    icon: ImageVector,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        icon, 
                        null, 
                        tint = MaterialTheme.colorScheme.primary, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        title, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold
                    )
                }
                trailing?.invoke()
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun ProductImagePicker(uri: String?, onPickImage: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F5F9))
            .clickable { onPickImage() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
            Icon(
                Icons.Default.Edit,
                null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Thêm ảnh sản phẩm",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownField(
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
            placeholder = { Text("Chọn danh mục sản phẩm") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(Color.White)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = { 
                        onCategorySelect(category.id)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun VariantManagerContent(
    attributes: List<AttributeInput>,
    variants: List<ProductVariant>,
    onAddAttribute: () -> Unit,
    onUpdateAttributeName: (Int, String) -> Unit,
    onAddAttributeValue: (Int, String) -> Unit,
    onRemoveAttribute: (Int) -> Unit,
    onRemoveAttributeValue: (Int, Int) -> Unit,
    onNavigateToVariants: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        attributes.forEachIndexed { index, attr ->
            AttributeEditItem(
                attr = attr,
                onNameChange = { onUpdateAttributeName(index, it) },
                onAddValue = { onAddAttributeValue(index, it) },
                onDelete = { onRemoveAttribute(index) },
                onDeleteValue = { onRemoveAttributeValue(index, it) }
            )
        }

        if (attributes.size < 3) {
            OutlinedButton(
                onClick = onAddAttribute,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm thuộc tính (Màu sắc, Size...)")
            }
        }

        if (variants.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${variants.size} biến thể đã được tạo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToVariants) {
                    Text("Thiết lập giá")
                    Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AttributeEditItem(
    attr: AttributeInput,
    onNameChange: (String) -> Unit,
    onAddValue: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteValue: (Int) -> Unit
) {
    var newValue by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = attr.name,
                    onValueChange = onNameChange,
                    placeholder = { Text("Tên thuộc tính (VD: Màu sắc)") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteSweep, null, tint = Color.Red.copy(alpha = 0.6f))
                }
            }
            
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attr.values.forEachIndexed { index, value ->
                    SuggestionChip(
                        onClick = { onDeleteValue(index) },
                        label = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(value)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp))
                            }
                        }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    placeholder = { Text("Giá trị (VD: Đỏ, Xanh...)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { 
                        if (newValue.isNotBlank()) { onAddValue(newValue); newValue = "" } 
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { if (newValue.isNotBlank()) { onAddValue(newValue); newValue = "" } },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    }
}

@Composable
fun SimplePricingForm(
    variant: ProductVariant,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppOutlinedTextField(
                value = if (variant.costPrice == 0.0) "" else variant.costPrice.toString(),
                onValueChange = { onUpdate(variant.price, it.toDoubleOrNull() ?: 0.0, variant.stock, variant.sku, variant.barcode) },
                label = { Text("Giá vốn") },
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
                value = variant.stock.toString(),
                onValueChange = { onUpdate(variant.price, variant.costPrice, it.toIntOrNull() ?: 0, variant.sku, variant.barcode) },
                label = { Text("Tồn kho") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            AppOutlinedTextField(
                value = variant.sku ?: "",
                onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, it, variant.barcode) },
                label = { Text("SKU") },
                modifier = Modifier.weight(1f)
            )
        }

        AppOutlinedTextField(
            value = variant.barcode ?: "",
            onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, variant.sku, it) },
            label = { Text("Mã vạch (Barcode)") },
            trailingIcon = {
                IconButton(onClick = onScanBarcode) {
                    Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BarcodeScannerDialog(onDismiss: () -> Unit, onBarcodeScanned: (String) -> Unit) {
    Dialog(
        onDismissRequest = onDismiss, 
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            BarcodeScannerView(modifier = Modifier.fillMaxSize(), onBarcodeScanned = onBarcodeScanned)
            BarcodeScanningOverlay(modifier = Modifier.fillMaxSize())
            IconButton(
                onClick = onDismiss, 
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) { 
                Icon(Icons.Default.Close, null, tint = Color.White) 
            }
            Text(
                "Đưa mã vạch vào khung quét", 
                color = Color.White, 
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp)
            )
        }
    }
}
