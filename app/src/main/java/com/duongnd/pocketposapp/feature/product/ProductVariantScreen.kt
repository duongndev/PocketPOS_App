package com.duongnd.pocketposapp.feature.product

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.domain.model.ProductVariant
import java.text.NumberFormat
import java.util.*

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
    val primaryColor = MaterialTheme.colorScheme.primary
    val vnFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))

    val filteredVariants = variants.filter { variant ->
        variant.attributes.any { it.value.contains(searchQuery, ignoreCase = true) } ||
                variant.sku?.contains(searchQuery, ignoreCase = true) == true ||
                searchQuery.isEmpty()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor, primaryColor.copy(alpha = 0.8f))
                        )
                    )
                    .statusBarsPadding()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Quản lý biến thể",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Text(
                                productName,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Button(
                            onClick = onSave,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                        ) {
                            Text("XONG", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Search Bar
                    VariantSearchCard(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(primaryColor.copy(alpha = 0.8f))
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF5F7F9),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Stats row
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            VariantStatCard(
                                label = "Biến thể",
                                value = variants.size.toString(),
                                icon = Icons.Default.Style,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.weight(1f)
                            )
                            VariantStatCard(
                                label = "Tổng tồn kho",
                                value = variants.sumOf { it.stock }.toString(),
                                icon = Icons.Default.Inventory2,
                                color = Color(0xFF388E3C),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    itemsIndexed(filteredVariants) { _, variant ->
                        val originalIndex = variants.indexOf(variant)
                        ModernVariantEditCard(
                            variant = variant,
                            format = vnFormat,
                            onUpdate = { updatedVariant ->
                                onUpdateVariant(originalIndex, updatedVariant)
                            }
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun VariantSearchCard(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Tìm theo tên hoặc SKU...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, null, tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun VariantStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ModernVariantEditCard(
    variant: ProductVariant,
    format: NumberFormat,
    onUpdate: (ProductVariant) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val displayName = variant.attributes.joinToString(" - ") { it.value }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isExpanded) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Style, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        displayName.ifEmpty { "Biến thể mặc định" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "SKU: ${variant.sku ?: "N/A"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null,
                        tint = Color.Gray
                    )
                }
            }

            if (!isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Giá bán", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(format.format(variant.price), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Tồn kho", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(
                            "${variant.stock} ${variant.unit}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (variant.stock <= 5) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFF1F3F5))

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

                    // Profit display
                    val profit = variant.price - variant.costPrice
                    if (profit > 0) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Lợi nhuận ước tính:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E7D32))
                                Text(format.format(profit), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppOutlinedTextField(
                            value = variant.stock.toString(),
                            onValueChange = { onUpdate(variant.copy(stock = it.toIntOrNull() ?: 0)) },
                            label = { Text("Số lượng tồn") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        AppOutlinedTextField(
                            value = variant.unit,
                            onValueChange = { onUpdate(variant.copy(unit = it)) },
                            label = { Text("Đơn vị tính") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    AppOutlinedTextField(
                        value = variant.sku ?: "",
                        onValueChange = { onUpdate(variant.copy(sku = it)) },
                        label = { Text("Mã SKU") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    AppOutlinedTextField(
                        value = variant.barcode ?: "",
                        onValueChange = { onUpdate(variant.copy(barcode = it)) },
                        label = { Text("Mã vạch / Barcode") },
                        trailingIcon = {
                            IconButton(onClick = { /* Scan logic */ }) {
                                Icon(Icons.Default.QrCodeScanner, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF8F9FA),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE9ECEF))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Trạng thái bán hàng", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(if (variant.isActive) "Đang kinh doanh" else "Ngừng kinh doanh", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                            Switch(
                                checked = variant.isActive,
                                onCheckedChange = { onUpdate(variant.copy(isActive = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
    }
}
