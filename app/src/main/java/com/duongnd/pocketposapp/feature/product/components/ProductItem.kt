package com.duongnd.pocketposapp.feature.product.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.domain.model.Product
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductItem(
    product: Product,
    onProductClick: (Product) -> Unit,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    var showMenu by remember { mutableStateOf(false) }

    val totalStock = product.variants.sumOf { it.stock }
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0
    val maxPrice = product.variants.maxOfOrNull { it.price } ?: 0.0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        onClick = { onProductClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Low stock indicator badge on image
                if (totalStock <= 10) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .size(8.dp)
                            .background(MaterialTheme.colorScheme.error, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.outline)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Chỉnh sửa") },
                                onClick = {
                                    showMenu = false
                                    onEditClick(product)
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Xóa", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick(product)
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                            )
                        }
                    }
                }

                if (product.categoryName.isNotEmpty()) {
                    Text(
                        text = product.categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val priceText = if (minPrice == maxPrice) {
                            currencyFormatter.format(minPrice)
                        } else {
                            "${currencyFormatter.format(minPrice)} - ${currencyFormatter.format(maxPrice)}"
                        }
                        Text(
                            text = priceText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        color = if (totalStock > 10) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                                else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Kho: $totalStock",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (totalStock > 10) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

