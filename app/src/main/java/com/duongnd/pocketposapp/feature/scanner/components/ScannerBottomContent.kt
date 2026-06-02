package com.duongnd.pocketposapp.feature.scanner.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongnd.pocketposapp.core.utils.formatPrice
import com.duongnd.pocketposapp.feature.scanner.ScannedItem

@Composable
fun ScannerBottomContent(
    modifier: Modifier = Modifier,
    scannedItems: List<ScannedItem> = emptyList(),
    totalPrice: Double = 0.0,
    onIncrease: (String) -> Unit = {},
    onDecrease: (String) -> Unit = {},
    onRemove: (String) -> Unit = {},
    onReviewOrder: () -> Unit
) {
    val totalCount = scannedItems.size
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Tính toán tỉ lệ scale dựa trên màn hình chuẩn (360dp x 640dp)
    val scaleFactor = (screenWidth.value / 360f).coerceIn(0.8f, 1.2f)
    
    val horizontalPadding = (20 * scaleFactor).dp
    val topPadding = if (screenHeight < 640.dp) 8.dp else 12.dp
    val itemSpacing = if (screenHeight < 640.dp) 8.dp else 12.dp
    val buttonHeight = if (screenHeight < 640.dp) 48.dp else 56.dp

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .fillMaxSize()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .padding(vertical = topPadding)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    .align(Alignment.CenterHorizontally)
            )

            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = itemSpacing),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Giỏ hàng",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (22 * scaleFactor).sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "$totalCount mục sản phẩm",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = (14 * scaleFactor).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = formatPrice(totalPrice.toLong()) + " đ",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = (16 * scaleFactor).sp
                        )
                    )
                }
            }

            // Scanned List
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (scannedItems.isEmpty()) {
                    EmptyScannedState(scaleFactor)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(itemSpacing),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(scannedItems, key = { it.barcode }) { item ->
                            ScannedBarcodeItem(
                                item = item,
                                scaleFactor = scaleFactor,
                                onIncrease = { onIncrease(item.barcode) },
                                onDecrease = { onDecrease(item.barcode) },
                                onRemove = { onRemove(item.barcode) }
                            )
                        }
                    }
                }
            }

            // Checkout Button
            Button(
                onClick = onReviewOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (screenHeight < 640.dp) 16.dp else 24.dp, top = 8.dp)
                    .height(buttonHeight),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (totalCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (totalCount > 0) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                ),
                enabled = totalCount > 0,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Thanh toán ngay",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = (16 * scaleFactor).sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size((20 * scaleFactor).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScannedBarcodeItem(
    item: ScannedItem,
    scaleFactor: Float,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, 
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding((12 * scaleFactor).dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Icon Placeholder
            Box(
                modifier = Modifier
                    .size((48 * scaleFactor).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size((24 * scaleFactor).dp)
                )
            }
            
            Spacer(modifier = Modifier.width((12 * scaleFactor).dp))
            
            // Item Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (15 * scaleFactor).sp,
                        lineHeight = (18 * scaleFactor).sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatPrice(item.price.toLong()) + " đ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = (14 * scaleFactor).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))

            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((4 * scaleFactor).dp)
            ) {
                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((6 * scaleFactor).dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size((28 * scaleFactor).dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Giảm",
                                    modifier = Modifier.size((14 * scaleFactor).dp),
                                    tint = if (item.count > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                    
                    Text(
                        text = "${item.count}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = (15 * scaleFactor).sp
                        ),
                        modifier = Modifier.widthIn(min = (20 * scaleFactor).dp),
                        textAlign = TextAlign.Center
                    )
                    
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size((28 * scaleFactor).dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tăng",
                                    modifier = Modifier.size((14 * scaleFactor).dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Delete Button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size((32 * scaleFactor).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size((20 * scaleFactor).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyScannedState(scaleFactor: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size((80 * scaleFactor).dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size((36 * scaleFactor).dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Giỏ hàng trống",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (18 * scaleFactor).sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Hãy quét mã vạch sản phẩm\nđể thêm vào giỏ hàng",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = (14 * scaleFactor).sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScannerBottomContent() {
    ScannerBottomContent(
        modifier = Modifier.height(500.dp),
        scannedItems = listOf(
            ScannedItem("1", "8934567890123", "Coca Cola 330ml", 10000.0, 1),
            ScannedItem("2", "8934567890456", "Bánh snack khoai tây vị phô mai", 5000.0, 2)
        ),
        totalPrice = 20000.0,
        onReviewOrder = {}
    )
}
