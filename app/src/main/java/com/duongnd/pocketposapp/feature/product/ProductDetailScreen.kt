package com.duongnd.pocketposapp.feature.product

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.product?.name ?: "Chi tiết sản phẩm",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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
        val scrollState = rememberScrollState()
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(primaryColor)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else if (state.error != null) {
                ErrorMessage(
                    message = state.error!!,
                    onRetry = { /* Retry logic */ },
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                state.product?.let { product ->
                    ProductDetailScrollContent(product, scrollState)
                }
            }
        }
    }
}

@Composable
fun ProductDetailScrollContent(product: Product, scrollState: ScrollState) {
    val vnFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            if (!product.imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = product.imageUri,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Inventory,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                }
            }
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                            startY = 400f
                        )
                    )
            )

            // Info on Image
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                StatusChip(isActive = product.variants.any { it.isActive })
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = product.categoryName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Information Cards
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
//            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Key Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val totalStock = product.variants.sumOf { it.stock }
                    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0
                    
                    QuickStatItem(
                        label = "Tồn kho",
                        value = "$totalStock",
                        subValue = product.variants.firstOrNull()?.unit ?: "đv",
                        icon = Icons.Default.Inventory2,
                        modifier = Modifier.weight(1f),
                        color = if (totalStock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    QuickStatItem(
                        label = "Giá bán từ",
                        value = vnFormat.format(minPrice).replace("₫", ""),
                        subValue = "VNĐ",
                        icon = Icons.Default.Payments,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFF4CAF50)
                    )
                }

                // Brand & Details
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow(icon = Icons.Default.BrandingWatermark, label = "Thương hiệu", value = product.brand.ifBlank { "N/A" })
                    DetailRow(icon = Icons.Default.QrCode, label = "Mã sản phẩm", value = product.id.takeLast(8).uppercase())
                }

                // Description Card
                if (!product.description.isNullOrBlank()) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Mô tả",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Variants Section
                Column {
                    Text(
                        text = "Phân loại hàng (${product.variants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    product.variants.chunked(2).forEach { rowVariants ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowVariants.forEach { variant ->
                                Box(modifier = Modifier.weight(1f)) {
                                    VariantCard(variant, vnFormat)
                                }
                            }
                            if (rowVariants.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(80.dp)) // Padding for FAB
            }
        }
    }
}

@Composable
fun QuickStatItem(
    label: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = color)
                Spacer(modifier = Modifier.width(2.dp))
                Text(subValue, style = MaterialTheme.typography.labelSmall, color = color, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun VariantCard(variant: ProductVariant, format: NumberFormat) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = variant.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = if (variant.stock > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (variant.stock > 0) "Tồn: ${variant.stock}" else "Hết",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (variant.stock > 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold
                )
            }

            if (variant.attributes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = variant.attributes.joinToString(", ") { it.value },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Text("Giá bán", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(
                text = format.format(variant.price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            
            Text(
                text = "Vốn: ${format.format(variant.costPrice)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusChip(isActive: Boolean) {
    Surface(
        color = if (isActive) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFF44336).copy(alpha = 0.2f),
        shape = CircleShape,
        border = BorderStroke(1.dp, if (isActive) Color(0xFF4CAF50) else Color(0xFFF44336))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color(0xFF4CAF50) else Color(0xFFF44336))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isActive) "Đang bán" else "Ngừng bán",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.ErrorOutline, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Thử lại")
        }
    }
}
