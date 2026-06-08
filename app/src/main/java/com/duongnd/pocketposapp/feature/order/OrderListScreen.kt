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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.utils.OrderStatus
import com.duongnd.pocketposapp.core.utils.formatPaymentMethod
import com.duongnd.pocketposapp.feature.order.components.OrderItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val statuses = listOf("Tất cả", "Hoàn thành", "Chờ thanh toán", "Đã hủy")

    val filteredOrders = remember(state.orders, state.searchQuery, state.selectedStatus) {
        state.orders.filter { order ->
            val matchesSearch = order.orderNumber.contains(state.searchQuery, ignoreCase = true)
            val orderStatus = OrderStatus.fromString(order.status)
            val matchesStatus = state.selectedStatus == "Tất cả" || orderStatus.label == state.selectedStatus
            matchesSearch && matchesStatus
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF1F5F9),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                        }
                        Text(
                            "Danh sách đơn hàng",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        IconButton(onClick = { viewModel.loadOrders() }) {
                            Icon(Icons.Default.Refresh, "Refresh", tint = Color.White)
                        }
                    }

                    // Search Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        TextField(
                            value = state.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("Tìm kiếm mã đơn...") },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                            trailingIcon = {
                                if (state.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, null)
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

                    // Filter Status Tabs
                    ScrollableTabRow(
                        selectedTabIndex = statuses.indexOf(state.selectedStatus),
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[statuses.indexOf(state.selectedStatus)]),
                                color = Color.White
                            )
                        },
                        divider = {}
                    ) {
                        statuses.forEach { status ->
                            Tab(
                                selected = state.selectedStatus == status,
                                onClick = { viewModel.onStatusChange(status) },
                                text = {
                                    Text(
                                        status,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (state.selectedStatus == status) FontWeight.Bold else FontWeight.Normal,
                                        color = if (state.selectedStatus == status) Color.White else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (filteredOrders.isEmpty()) {
                EmptyOrderState(isSearching = state.searchQuery.isNotEmpty() || state.selectedStatus != "Tất cả")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.id }) { order ->
                        val orderStatus = OrderStatus.fromString(order.status)
                        OrderItem(
                            orderNumber = order.orderNumber,
                            customerName = null,
                            totalAmount = order.totalAmount.toDouble(),
                            createdAt = formatDate(order.createdAt),
                            statusLabel = orderStatus.label,
                            statusColor = orderStatus.color,
                            paymentMethod = formatPaymentMethod(order.paymentMethod),
                            onClick = {
                                navController.navigate(
                                    Routes.ORDER_DETAIL.replace(
                                        "{orderId}",
                                        order.id
                                    )
                                )
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            if (state.error != null) {
                Text(
                    text = "Lỗi: ${state.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }
        }
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        if (date != null) outputFormat.format(date) else dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun EmptyOrderState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
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
