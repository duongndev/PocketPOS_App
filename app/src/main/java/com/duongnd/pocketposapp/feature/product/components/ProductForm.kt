package com.duongnd.pocketposapp.feature.product.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.ProductVariant
import com.duongnd.pocketposapp.feature.product.AddEditProductState
import com.duongnd.pocketposapp.feature.product.AttributeInput

@Composable
fun ProductFormContent(
    state: AddEditProductState,
    currentStep: Int,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onImageChange: (String?) -> Unit,
    onHasVariantsChange: (Boolean) -> Unit,
    onAddAttribute: () -> Unit,
    onUpdateAttributeName: (Int, String) -> Unit,
    onAddAttributeValue: (Int, String) -> Unit,
    onRemoveAttribute: (Int) -> Unit,
    onRemoveAttributeValue: (Int, Int) -> Unit,
    onUpdateVariant: (Int, Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: (Int) -> Unit,
    onCreateCategory: (String) -> Unit
) {
    var showCategorySheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        when (currentStep) {
            0 -> BasicInfoStep(
                state = state,
                onNameChange = onNameChange,
                onDescriptionChange = onDescriptionChange,
                onCategoryClick = { showCategorySheet = true },
                onCategorySelect = onCategorySelect,
                onImageChange = onImageChange
            )
            1 -> VariantsStep(
                state = state,
                onHasVariantsChange = onHasVariantsChange,
                onAddAttribute = onAddAttribute,
                onUpdateAttributeName = onUpdateAttributeName,
                onAddAttributeValue = onAddAttributeValue,
                onRemoveAttribute = onRemoveAttribute,
                onRemoveAttributeValue = onRemoveAttributeValue,
                onUpdateVariant = onUpdateVariant,
                onScanBarcode = onScanBarcode
            )
        }
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
fun BasicInfoStep(
    state: AddEditProductState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCategoryClick: () -> Unit,
    onCategorySelect: (String) -> Unit,
    onImageChange: (String?) -> Unit
) {
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onImageChange(uri?.toString()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Hero Image Picker
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
                        Surface(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            shadowElevation = 4.dp
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.padding(6.dp).size(16.dp), tint = Color.White)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                            Text("Thêm ảnh", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                if (state.imageUri != null) {
                    TextButton(onClick = { onImageChange(null) }) {
                        Text("Xóa ảnh", color = Color.Red.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Form Fields
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Thông tin định danh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    AppOutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        label = { Text("Tên sản phẩm *") },
                        placeholder = { Text("Ví dụ: Trà Đào Cam Sả") },
                        leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) }
                    )

                    CategorySelectionField(
                        selectedCategoryId = state.selectedCategoryId,
                        categories = state.categories,
                        onClick = onCategoryClick,
                        onCategorySelect = onCategorySelect
                    )

                    AppOutlinedTextField(
                        value = state.description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Mô tả sản phẩm") },
                        placeholder = { Text("Mô tả ngắn gọn về sản phẩm...") },
                        singleLine = false,
                        minLines = 3,
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Notes, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)) }
                    )
                }
            }
        }
    }
}

@Composable
fun VariantsStep(
    state: AddEditProductState,
    onHasVariantsChange: (Boolean) -> Unit,
    onAddAttribute: () -> Unit,
    onUpdateAttributeName: (Int, String) -> Unit,
    onAddAttributeValue: (Int, String) -> Unit,
    onRemoveAttribute: (Int) -> Unit,
    onRemoveAttributeValue: (Int, Int) -> Unit,
    onUpdateVariant: (Int, Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: (Int) -> Unit
) {
    var showBulkEdit by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Switch Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = if (state.hasVariants) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.White,
                border = BorderStroke(1.dp, if (state.hasVariants) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).background(if (state.hasVariants) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Layers,
                            null,
                            tint = if (state.hasVariants) Color.White else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Phân loại hàng hóa", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Kích hoạt nếu có Size, Màu sắc...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                    Switch(checked = state.hasVariants, onCheckedChange = onHasVariantsChange)
                }
            }
        }

        if (state.hasVariants) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
                    Text("Thuộc tính", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.attributes.size < 3) {
                        Button(
                            onClick = onAddAttribute,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thêm", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            itemsIndexed(state.attributes) { index, attr ->
                ModernAttributeItem(
                    attr = attr,
                    onNameChange = { onUpdateAttributeName(index, it) },
                    onAddValue = { onAddAttributeValue(index, it) },
                    onDelete = { onRemoveAttribute(index) },
                    onDeleteValue = { onRemoveAttributeValue(index, it) }
                )
            }

            if (state.variants.isNotEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Biến thể (${state.variants.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = { showBulkEdit = !showBulkEdit }) {
                            Text(if (showBulkEdit) "Đóng áp dụng nhanh" else "Áp dụng giá nhanh", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (showBulkEdit) {
                    item {
                        BulkEditCard(
                            onApply = { price, cost, stock ->
                                state.variants.forEachIndexed { index, _ ->
                                    onUpdateVariant(index, price, cost, stock, null, null)
                                }
                                showBulkEdit = false
                            }
                        )
                    }
                }

                itemsIndexed(state.variants) { index, variant ->
                    ExpandableVariantItem(
                        variant = variant,
                        index = index,
                        onUpdate = { p, cp, st, sku, bc -> onUpdateVariant(index, p, cp, st, sku, bc) },
                        onScanBarcode = { onScanBarcode(index) }
                    )
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Giá & Kho hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))
                        if (state.variants.isNotEmpty()) {
                            SimplePricingForm(
                                variant = state.variants.first(),
                                onUpdate = { p, cp, st, sku, bc -> onUpdateVariant(0, p, cp, st, sku, bc) },
                                onScanBarcode = { onScanBarcode(0) }
                            )
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun BulkEditCard(onApply: (Double, Double, Int) -> Unit) {
    var price by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppOutlinedTextField(
                    value = cost, onValueChange = { cost = it }, label = { Text("Giá vốn", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AppOutlinedTextField(
                    value = price, onValueChange = { price = it }, label = { Text("Giá bán", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AppOutlinedTextField(
                    value = stock, onValueChange = { stock = it }, label = { Text("Kho", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Button(
                onClick = { onApply(price.toDoubleOrNull() ?: 0.0, cost.toDoubleOrNull() ?: 0.0, stock.toIntOrNull() ?: 0) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("ÁP DỤNG CHO TẤT CẢ BIẾN THỂ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAttributeItem(
    attr: AttributeInput,
    onNameChange: (String) -> Unit,
    onAddValue: (String) -> Unit,
    onDelete: () -> Unit,
    onDeleteValue: (Int) -> Unit
) {
    var newValue by remember { mutableStateOf("") }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = attr.name,
                    onValueChange = onNameChange,
                    placeholder = { Text("Tên (Ví dụ: Size)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFEF4444).copy(alpha = 0.7f))
                }
            }
            
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attr.values.forEachIndexed { index, value ->
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { onDeleteValue(index) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppOutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    placeholder = { Text("Thêm giá trị (Ví dụ: S, M, L)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (newValue.isNotBlank()) { onAddValue(newValue); newValue = "" }
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { if (newValue.isNotBlank()) { onAddValue(newValue); newValue = "" } },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(52.dp)
                ) {
                    Icon(Icons.Default.Add, null)
                }
            }
        }
    }
}

@Composable
fun ExpandableVariantItem(
    variant: ProductVariant,
    index: Int,
    onUpdate: (Double, Double, Int, String?, String?) -> Unit,
    onScanBarcode: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, if (expanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("${index + 1}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val variantName = variant.attributes.joinToString(" • ") { it.value }
                    Text(
                        text = variantName.ifBlank { "Biến thể ${index + 1}" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("${variant.price} ₫", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                        Text("Kho: ${variant.stock}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Icon(
                    Icons.Default.ExpandMore,
                    null,
                    modifier = Modifier.rotate(rotation),
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp), color = Color(0xFFF1F5F9))
                    SimplePricingForm(variant = variant, onUpdate = onUpdate, onScanBarcode = onScanBarcode)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CategorySelectionField(
    selectedCategoryId: String?,
    categories: List<Category>,
    onClick: () -> Unit,
    onCategorySelect: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val displayedCategories = if (isExpanded) categories else categories.take(8)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(vertical = 4.dp)
        ) {
            Icon(
                Icons.Default.Category,
                null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Danh mục sản phẩm *",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                if (categories.isEmpty()) "Thêm mới" else "Xem tất cả",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                Icons.Default.ChevronRight,
                null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (categories.isEmpty()) {
            Surface(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CloudOff, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Chưa có dữ liệu danh mục. Nhấn để tạo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                displayedCategories.forEach { category ->
                    val isSelected = category.id == selectedCategoryId
                    Surface(
                        onClick = { onCategorySelect(category.id) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier.animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    null,
                                    modifier = Modifier.size(16.dp).padding(end = 6.dp),
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }

                if (categories.size > 8) {
                    Surface(
                        onClick = { isExpanded = !isExpanded },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.animateContentSize()
                    ) {
                        Text(
                            text = if (isExpanded) "Thu gọn" else "Xem thêm (${categories.size - 8})",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    val filteredCategories = remember(searchQuery, categories) {
        if (searchQuery.isBlank()) categories
        else categories.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(horizontal = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Danh mục", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showAddCategoryDialog = true }) {
                    Icon(Icons.Default.AddCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF1F5F9),
                    focusedContainerColor = Color(0xFFF1F5F9),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(filteredCategories) { _, category ->
                    val isSelected = category.id == selectedCategoryId
                    Surface(
                        onClick = { onCategorySelect(category.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null
                    ) {
                        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(20.dp).border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape).padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary, CircleShape))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(category.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Thêm danh mục mới") },
            text = { 
                AppOutlinedTextField(
                    value = newCategoryName, 
                    onValueChange = { newCategoryName = it }, 
                    label = { Text("Tên danh mục") },
                    placeholder = { Text("Ví dụ: Cà phê, Sinh tố...") }
                ) 
            },
            confirmButton = { 
                Button(
                    onClick = { 
                        if (newCategoryName.isNotBlank()) { 
                            onCreateCategory(newCategoryName)
                            showAddCategoryDialog = false
                            newCategoryName = "" 
                        } 
                    },
                    shape = RoundedCornerShape(12.dp)
                ) { Text("XÁC NHẬN") } 
            },
            dismissButton = { 
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("HỦY") } 
            },
            shape = RoundedCornerShape(24.dp)
        )
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text("₫", modifier = Modifier.padding(start = 12.dp), color = Color.Gray, fontWeight = FontWeight.Bold) }
            )
            AppOutlinedTextField(
                value = if (variant.price == 0.0) "" else variant.price.toString(),
                onValueChange = { onUpdate(it.toDoubleOrNull() ?: 0.0, variant.costPrice, variant.stock, variant.sku, variant.barcode) },
                label = { Text("Giá bán") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Text("₫", modifier = Modifier.padding(start = 12.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AppOutlinedTextField(
                value = variant.stock.toString(),
                onValueChange = { onUpdate(variant.price, variant.costPrice, it.toIntOrNull() ?: 0, variant.sku, variant.barcode) },
                label = { Text("Số lượng") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Inventory, null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
            )
            AppOutlinedTextField(
                value = variant.sku ?: "",
                onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, it, variant.barcode) },
                label = { Text("Mã SKU") },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tự động") },
                leadingIcon = { Icon(Icons.Default.Label, null, modifier = Modifier.size(18.dp), tint = Color.Gray) }
            )
        }
        AppOutlinedTextField(
            value = variant.barcode ?: "",
            onValueChange = { onUpdate(variant.price, variant.costPrice, variant.stock, variant.sku, it) },
            label = { Text("Mã vạch / Barcode") },
            leadingIcon = { Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp), tint = Color.Gray) },
            trailingIcon = { 
                IconButton(onClick = onScanBarcode) { 
                    Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary) 
                } 
            }
        )
    }
}
