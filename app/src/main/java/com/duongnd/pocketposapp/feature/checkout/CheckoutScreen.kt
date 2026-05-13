package com.duongnd.pocketposapp.feature.checkout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.duongnd.pocketposapp.core.ui.theme.PocketPOSAppTheme
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.utils.formatPrice
import com.duongnd.pocketposapp.feature.scanner.ScanViewModel
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: ScanViewModel
) {
    val items by viewModel.scannedItems.collectAsState()
    CheckoutContent(
        items = items,
        onBackClick = { navController.popBackStack() },
        onConfirmPayment = { /* TODO: Process Payment */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    items: List<ScannedItem>,
    onBackClick: () -> Unit,
    onConfirmPayment: (String) -> Unit,
    initialPaymentMethod: String? = null
) {
    val totalPrice = items.sumOf { it.price * it.count }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(initialPaymentMethod) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val currentDate = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            CheckoutTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            CheckoutBottomContent(
                totalPrice = totalPrice,
                selectedPaymentMethod = selectedPaymentMethod,
                onConfirmPayment = onConfirmPayment
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        
        // Auto-scroll to bottom when QR code appears
        LaunchedEffect(selectedPaymentMethod) {
            if (selectedPaymentMethod == "QR") {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            ReceiptCard(
                items = items,
                totalPrice = totalPrice.toLong(),
                currentDate = currentDate
            )

            Spacer(modifier = Modifier.height(32.dp))

            PaymentMethodSelection(
                selectedPaymentMethod = selectedPaymentMethod,
                onPaymentMethodSelect = { selectedPaymentMethod = it }
            )

            // Dynamic spacer to push content up when QR is shown in bottom bar
            // Increased height to ensure everything scrolls above the tall bottom sheet
            if (selectedPaymentMethod == "QR") {
                Spacer(modifier = Modifier.height(400.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CheckoutTopBar(onBackClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                "Thanh toán",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun ReceiptCard(
    items: List<ScannedItem>,
    totalPrice: Long,
    currentDate: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Store Info
            Text(
                text = "POCKET POS",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Text(
                text = "123 Đường ABC, Hà Nội",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = "ĐT: 0987.654.321",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Order Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Số HD: #8899", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(currentDate, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items Header
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Mặt hàng",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "SL",
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "T.Tiền",
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items List
            items.forEach { item ->
                ReceiptItemRow(item)
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TỔNG CỘNG",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    formatPrice(totalPrice) + " đ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Cảm ơn quý khách. Hẹn gặp lại!",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ReceiptItemRow(item: ScannedItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium)
            Text(formatPrice(item.price.toLong()), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(
            "${item.count}",
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            formatPrice((item.price * item.count).toLong()),
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentMethodSelection(
    selectedPaymentMethod: String?,
    onPaymentMethodSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(modifier = modifier) {
        Text(
            "Phương thức thanh toán",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        PaymentMethodItem(
            title = "Tiền mặt",
            icon = Icons.Default.Payments,
            isSelected = selectedPaymentMethod == "CASH",
            onClick = { onPaymentMethodSelect("CASH") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethodItem(
            title = "Chuyển khoản / QR Code",
            icon = Icons.Default.QrCode2,
            isSelected = selectedPaymentMethod == "QR",
            onClick = { onPaymentMethodSelect("QR") }
        )
    }
}

@Composable
fun CheckoutBottomContent(
    totalPrice: Double,
    selectedPaymentMethod: String?,
    onConfirmPayment: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            AnimatedVisibility(
                visible = selectedPaymentMethod == "QR",
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quét mã để thanh toán",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF506490),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = "Payment QR Code",
                        modifier = Modifier.size(200.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "STK: 123456789 - MB Bank",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng thanh toán",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = formatPrice(totalPrice.toLong()) + " đ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF506490)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { selectedPaymentMethod?.let { onConfirmPayment(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = selectedPaymentMethod != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF506490),
                    disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    "THANH TOÁN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DashedDivider() {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.LightGray,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) {
        Color(0xFFE8EDF8)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val borderColor = if (isSelected) {
        Color(0xFF506490)
    } else {
        Color.LightGray.copy(alpha = 0.5f)
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (isSelected) Color(0xFF506490) else Color(0xFFF1F3F5),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = Color.Black
            )
            
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF506490),
                    unselectedColor = Color.LightGray
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenQRPreview() {
    val sampleItems = listOf(
        ScannedItem("123456", "Sữa tươi Vinamilk", 12000.0, 2),
        ScannedItem("789012", "Bánh mì gối", 15000.0, 1)
    )
    PocketPOSAppTheme {
        CheckoutContent(
            items = sampleItems,
            onBackClick = {},
            onConfirmPayment = {},
            initialPaymentMethod = "QR"
        )
    }
}
