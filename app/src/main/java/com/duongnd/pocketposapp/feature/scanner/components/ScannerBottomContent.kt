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
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    onReviewOrder: () -> Unit
) {
    val totalCount = scannedItems.sumOf { it.count }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally)
            )

            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Giỏ hàng",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                    )
                    Text(
                        text = "$totalCount mục đã quét",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = formatPrice(totalPrice.toLong()) + " đ",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // List Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (scannedItems.isEmpty()) {
                    EmptyScannedState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(scannedItems) { item ->
                            ScannedBarcodeItem(
                                item = item,
                                onIncrease = { onIncrease(item.barcode) },
                                onDecrease = { onDecrease(item.barcode) }
                            )
                        }
                    }
                }
            }

            // Bottom Action
            Button(
                onClick = onReviewOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (totalCount > 0) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5),
                    contentColor = if (totalCount > 0) Color.White else Color.Gray
                ),
                enabled = totalCount > 0,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Tiếp tục thanh toán",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ChevronRight, null)
                }
            }
        }
    }
}

@Composable
private fun ScannedBarcodeItem(
    item: ScannedItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8F9FA)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode,
                    contentDescription = null,
                    tint = Color(0xFF4A4A4A),
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                )
                Text(
                    text = formatPrice(item.price.toLong()) + " đ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onDecrease,
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp),
                            tint = if (item.count > 1) Color.Black else Color.Gray
                        )
                    }
                }
                
                Text(
                    text = "${item.count}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.widthIn(min = 20.dp),
                    textAlign = TextAlign.Center
                )
                
                Surface(
                    onClick = onIncrease,
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyScannedState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFF8F9FA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.LightGray
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Giỏ hàng đang trống",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vui lòng di chuyển máy ảnh đến vị trí\ncó mã vạch để bắt đầu.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                lineHeight = 18.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScannerBottomContent() {
    ScannerBottomContent(
        modifier = Modifier.height(500.dp),
        scannedItems = listOf(
            ScannedItem("8934567890123", "Coca Cola 330ml", 10000.0, 1),
            ScannedItem("8934567890456", "Bánh snack", 5000.0, 2)
        ),
        totalPrice = 20000.0,
        onReviewOrder = {}
    )
}
