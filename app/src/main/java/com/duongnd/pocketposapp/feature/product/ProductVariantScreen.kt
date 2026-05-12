package com.duongnd.pocketposapp.feature.product

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.domain.model.ProductVariant
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductVariantScreen(
    navController: NavController,
    productName: String,
    variants: List<ProductVariant>,
    onUpdateVariant: (Int, ProductVariant) -> Unit,
    onSave: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredVariants = variants.filter { variant ->
        variant.attributes.any { it.value.contains(searchQuery, ignoreCase = true) } ||
                variant.sku?.contains(searchQuery, ignoreCase = true) == true ||
                searchQuery.isEmpty()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Quản lý biến thể", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(productName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onSave) {
                        Text("XONG", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Search Bar & Stats in a Card
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        modifier = Modifier.padding(16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatItem(
                            label = "Tổng biến thể",
                            value = variants.size.toString(),
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Category
                        )
                        StatItem(
                            label = "Tổng tồn kho",
                            value = variants.sumOf { it.stock }.toString(),
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Inventory2
                        )
                    }
                }
            }

            // Variants List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(filteredVariants) { _, variant ->
                    val originalIndex = variants.indexOf(variant)
                    VariantDetailItem(
                        variant = variant,
                        onUpdate = { updatedVariant ->
                            onUpdateVariant(originalIndex, updatedVariant)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text("Tìm theo tên hoặc SKU...", fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, 
            contentDescription = null, 
            modifier = Modifier.size(24.dp), 
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VariantDetailItem(
    variant: ProductVariant,
    onUpdate: (ProductVariant) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val displayName = variant.attributes.joinToString(" - ") { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = if (isExpanded) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null,
        elevation = CardDefaults.cardElevation(if (isExpanded) 4.dp else 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        displayName.firstOrNull()?.toString()?.uppercase() ?: "V",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "SKU: ${variant.sku ?: "Không có"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            if (!isExpanded) {
                Row(
                    modifier = Modifier.padding(top = 12.dp, start = 52.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SmallInfoItem(
                        label = "Giá bán",
                        value = String.format(Locale.getDefault(), "%,.0f đ", variant.price)
                    )
                    SmallInfoItem(
                        label = "Tồn kho",
                        value = variant.stock.toString(),
                        valueColor = if (variant.stock <= 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppOutlinedTextField(
                            value = if (variant.costPrice == 0.0) "" else variant.costPrice.toString(),
                            onValueChange = { onUpdate(variant.copy(costPrice = it.toDoubleOrNull() ?: 0.0)) },
                            label = { Text("Giá nhập") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        AppOutlinedTextField(
                            value = if (variant.price == 0.0) "" else variant.price.toString(),
                            onValueChange = { onUpdate(variant.copy(price = it.toDoubleOrNull() ?: 0.0)) },
                            label = { Text("Giá bán") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppOutlinedTextField(
                            value = variant.stock.toString(),
                            onValueChange = { onUpdate(variant.copy(stock = it.toIntOrNull() ?: 0)) },
                            label = { Text("Số lượng tồn") },
                            modifier = Modifier.weight(0.4f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        AppOutlinedTextField(
                            value = variant.unit,
                            onValueChange = { onUpdate(variant.copy(unit = it)) },
                            label = { Text("Đơn vị tính") },
                            modifier = Modifier.weight(0.6f)
                        )
                    }

                    AppOutlinedTextField(
                        value = variant.sku ?: "",
                        onValueChange = { onUpdate(variant.copy(sku = it)) },
                        label = { Text("Mã SKU") }
                    )
                    
                    AppOutlinedTextField(
                        value = variant.barcode ?: "",
                        onValueChange = { onUpdate(variant.copy(barcode = it)) },
                        label = { Text("Mã vạch / Barcode") },
                        trailingIcon = {
                            IconButton(onClick = { /* Scan logic would go here if needed */ }) {
                                Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Trạng thái kinh doanh", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(if (variant.isActive) "Đang bán" else "Ngừng bán", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = variant.isActive,
                            onCheckedChange = { onUpdate(variant.copy(isActive = it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmallInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = valueColor)
    }
}
