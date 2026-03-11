package com.duongnd.pocketposapp.feature.product

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.data.remote.dto.CategoryDTO
import com.duongnd.pocketposapp.data.remote.dto.ProductDTO
import com.duongnd.pocketposapp.feature.product.components.ProductItem

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductDTO?>(null) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val columns = when {
        screenWidth < 600 -> 1
        screenWidth < 1000 -> 2
        else -> 3
    }

    // Dữ liệu mẫu (Mock Data)
    val mockProducts = remember {
        mutableStateListOf(
            ProductDTO(
                _id = "1",
                name = "Sữa Tươi Vinamilk 1L",
                barcode = "8934567890123",
                categoryId = CategoryDTO("1", "Sữa & Đồ uống", "", true, "", ""),
                price = 35000.0,
                costPrice = 28000.0,
                stock = 45,
                isActive = true,
                createdAt = "",
                updatedAt = ""
            ),
            ProductDTO(
                _id = "2",
                name = "Bánh Mì Sandwich",
                barcode = "8934567890456",
                categoryId = CategoryDTO("2", "Bánh kẹo", "", true, "", ""),
                price = 15000.0,
                costPrice = 10000.0,
                stock = 8,
                isActive = true,
                createdAt = "",
                updatedAt = ""
            ),
            ProductDTO(
                _id = "3",
                name = "Coca Cola 330ml",
                barcode = "8934567890789",
                categoryId = CategoryDTO("3", "Đồ uống", "", true, "", ""),
                price = 10000.0,
                costPrice = 7500.0,
                stock = 120,
                isActive = true,
                createdAt = "",
                updatedAt = ""
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Quản lý sản phẩm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("add_product") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Thêm mới") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            // Thanh tìm kiếm và lọc
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tìm tên hoặc mã vạch...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledIconButton(
                        onClick = { /* Logic lọc */ },
                        shape = MaterialTheme.shapes.medium,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Lọc",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Thống kê nhanh
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Tổng số lượng",
                    value = "${mockProducts.size}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
                SummaryCard(
                    title = "Sắp hết hàng",
                    value = "${mockProducts.count { it.stock < 10 }}",
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Danh sách sản phẩm
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(top = 10.dp, bottom = 88.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mockProducts) { product ->
                    ProductItem(
                        product = product,
                        onEditClick = { navController.navigate("edit_product/${it._id}") },
                        onDeleteClick = {
                            productToDelete = it
                            showDeleteConfirm = true
                        }
                    )
                }
            }
        }

        // Dialog xác nhận xóa
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Xác nhận xóa") },
                text = { Text("Bạn có chắc chắn muốn xóa sản phẩm '${productToDelete?.name}' không?") },
                confirmButton = {
                    Button(
                        onClick = {
                            mockProducts.remove(productToDelete)
                            showDeleteConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
