package com.duongnd.pocketposapp.feature.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.duongnd.pocketposapp.feature.checkout.components.ReceiptCard
import com.duongnd.pocketposapp.feature.scanner.ScanViewModel
import com.duongnd.pocketposapp.feature.scanner.ScannedItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String,
    viewModel: ScanViewModel = hiltViewModel()
) {
    // Mock Data mapped to ScannedItem for ReceiptCard compatibility
    val orderNumber = orderId // Use orderId as order number for demonstration
    val createdAt = "20/05/2024 14:30"
    
    val orderItems = remember {
        listOf(
            ScannedItem("1", "001", "Sữa tươi Vinamilk", 12000.0, 2),
            ScannedItem("2", "002", "Bánh mì gối", 15000.0, 1),
            ScannedItem("3", "003", "Trứng gà (vỉ 10)", 35000.0, 1),
            ScannedItem("4", "004", "Dầu ăn Simply 1L", 56000.0, 1),
            ScannedItem("5", "005", "Nước rửa bát Sunlight", 20000.0, 1)
        )
    }
    
    val totalPrice = orderItems.sumOf { it.price * it.count }.toLong()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, null, tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Button(
                        onClick = { /* Print action */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Print, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("In hóa đơn (Bluetooth/Wifi)")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F3F5)) // Slightly darker background to make receipt pop
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Badge
            Surface(
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "ĐƠN HÀNG HOÀN THÀNH",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }

            // The Receipt Card from Checkout feature
            ReceiptCard(
                items = orderItems,
                totalPrice = totalPrice,
                currentDate = createdAt,
                store = viewModel.store,
                orderNumber = orderNumber
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Additional Order Metadata if needed
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Thông tin bổ sung",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("Mã đơn hàng", orderNumber)
                    DetailRow("Thời gian", createdAt)
                    DetailRow("Phương thức", "Tiền mặt")
                    DetailRow("Người bán", "Nhân viên quầy 01")
                    DetailRow("Khách hàng", "Khách lẻ")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}
