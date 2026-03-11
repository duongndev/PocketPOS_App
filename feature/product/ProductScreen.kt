package com.duongnd.pocketposapp.feature.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.data.remote.dto.CategoryDTO
import com.duongnd.pocketposapp.data.remote.dto.ProductDTO
import com.duongnd.pocketposapp.feature.product.components.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    // Determine column count based on screen width (Responsive)
    val columns = when {
        screenWidth < 600 -> 1
        screenWidth < 900 -> 2
        else -> 3
    }

    // Mock Data for UI demonstration
    val mockProducts = remember {
        listOf(
            ProductDTO(
                _id = "1",
                name = "Sữa Tươi Tiệt Trùng Vinamilk 1L",
                barcode = 8934567890123,
                categoryId = CategoryDTO("1", "Sữa & Chế phẩm sữa", "", true, "", ""),
                price = 35000.0,
                costPrice = 28000.0,
                stock = 45,
                isActive = true,
                createdAt = "",
                updatedAt = ""
            ),
            ProductDTO(
                _id = "2",
                name = "Bánh Mì Sandwich Kinh Đô",
                barcode = 8934567890456,
                categoryId = CategoryDTO("2", "Bánh kẹo", "", true, "", ""),
                price = 15000.0,
                costPrice = 10000.0,
                stock = 5,
                isActive = true,
                createdAt = "",
                updatedAt = ""
            ),
            ProductDTO(
                _id = "3",
                name = "Nước Giải Khát Coca Cola 330ml",
                barcode = 8934567890789,
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
            TopAppBar(
                title = { Text("Quản lý sản phẩm") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Navigate to Add Product */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm sản phẩm")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search and Filter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tìm kiếm sản phẩm...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { /* Filter Logic */ },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.FilterAlt,
                        contentDescription = "Lọc",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Stats row (Responsive summary)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Tổng SP",
                    value = "${mockProducts.size}",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                SummaryCard(
                    title = "Sắp hết hàng",
                    value = "${mockProducts.count { it.stock < 10 }}",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.errorContainer
                )
            }

            // Product List
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mockProducts) { product ->
                    ProductItem(
                        product = product,
                        onEditClick = { /* Handle edit */ },
                        onDeleteClick = { /* Handle delete */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
