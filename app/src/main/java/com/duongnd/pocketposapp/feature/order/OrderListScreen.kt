package com.duongnd.pocketposapp.feature.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.feature.order.components.OrderItem

data class OrderUI(
    val id: String,
    val orderNumber: String,
    val customerName: String?,
    val totalAmount: Double,
    val createdAt: String,
    val status: OrderStatus,
    val paymentMethod: String
)

enum class OrderStatus(val label: String, val color: Color) {
    PENDING("Chờ thanh toán", Color(0xFFFFA000)),
    COMPLETED("Hoàn thành", Color(0xFF4CAF50)),
    CANCELLED("Đã hủy", Color(0xFFF44336))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Tất cả") }
    
    val statuses = listOf("Tất cả", "Hoàn thành", "Chờ thanh toán", "Đã hủy")
    
    // Mock Data
    val mockOrders = remember {
        listOf(
            OrderUI("1", "#ORD-9821", "Khách lẻ", 150000.0, "Hôm nay, 14:30", OrderStatus.COMPLETED, "Tiền mặt"),
            OrderUI("2", "#ORD-9820", "Anh Tú", 450000.0, "Hôm nay, 12:15", OrderStatus.PENDING, "Chuyển khoản (QR)"),
            OrderUI("3", "#ORD-9819", "Chị Lan", 85000.0, "Hôm nay, 10:00", OrderStatus.COMPLETED, "Tiền mặt"),
            OrderUI("4", "#ORD-9818", "Khách lẻ", 120000.0, "Hôm qua, 18:45", OrderStatus.CANCELLED, "Tiền mặt"),
            OrderUI("5", "#ORD-9817", "Khách lẻ", 210000.0, "Hôm qua, 16:20", OrderStatus.COMPLETED, "Tiền mặt"),
            OrderUI("6", "#ORD-9816", "Minh Hoàng", 55000.0, "Hôm qua, 09:10", OrderStatus.COMPLETED, "Tiền mặt"),
        )
    }

    val filteredOrders = remember(searchQuery, selectedStatus) {
        mockOrders.filter { order ->
            val matchesSearch = order.orderNumber.contains(searchQuery, ignoreCase = true) || 
                              (order.customerName?.contains(searchQuery, ignoreCase = true) ?: false)
            val matchesStatus = selectedStatus == "Tất cả" || order.status.label == selectedStatus
            matchesSearch && matchesStatus
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Danh sách đơn hàng",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
                
                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Tìm kiếm mã đơn, khách hàng...", color = Color.White.copy(alpha = 0.7f)) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }

                // Filter Status Tabs
                ScrollableTabRow(
                    selectedTabIndex = statuses.indexOf(selectedStatus),
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[statuses.indexOf(selectedStatus)]),
                            color = Color.White
                        )
                    },
                    divider = {}
                ) {
                    statuses.forEach { status ->
                        Tab(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status },
                            text = { 
                                Text(
                                    status, 
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedStatus == status) FontWeight.Bold else FontWeight.Normal
                                ) 
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            if (filteredOrders.isEmpty()) {
                EmptyOrderState(isSearching = searchQuery.isNotEmpty() || selectedStatus != "Tất cả")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        OrderItem(
                            orderNumber = order.orderNumber,
                            customerName = order.customerName,
                            totalAmount = order.totalAmount,
                            createdAt = order.createdAt,
                            statusLabel = order.status.label,
                            statusColor = order.status.color,
                            paymentMethod = order.paymentMethod,
                            onClick = { 
                                navController.navigate(Routes.ORDER_DETAIL.replace("{orderId}", order.id))
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyOrderState(isSearching: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (isSearching) "Không tìm thấy đơn hàng nào" else "Chưa có đơn hàng nào",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isSearching) "Hãy thử tìm kiếm với từ khóa hoặc trạng thái khác" 
                   else "Các đơn hàng bạn tạo sẽ xuất hiện tại đây",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
