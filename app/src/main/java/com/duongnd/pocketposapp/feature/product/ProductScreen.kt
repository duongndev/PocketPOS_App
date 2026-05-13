package com.duongnd.pocketposapp.feature.product

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val products = viewModel.productsPagingData.collectAsLazyPagingItems()

    ProductScreenContent(
        state = state,
        products = products,
        onOpenDrawer = onOpenDrawer,
        onProductClick = { navController.navigate("product_detail/${it.id}") },
        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
        onCategoryChange = { viewModel.onCategoryChange(it) },
        onRefresh = { products.refresh() },
        onAddProduct = { navController.navigate(Routes.ADD_PRODUCT) },
        onEditProduct = { navController.navigate("edit_product/${it.id}") },
        onDeleteProduct = { viewModel.deleteProduct(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    state: ProductState,
    products: androidx.paging.compose.LazyPagingItems<Product>,
    onOpenDrawer: () -> Unit,
    onProductClick: (Product) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var isGridView by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        Text(
                            "Kho Hàng",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        Row {
                            IconButton(onClick = { isGridView = !isGridView }) {
                                Icon(
                                    if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                                    contentDescription = "View Mode",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                            }
                        }
                    }

                    // Integrated Search Bar
                    SearchCard(
                        query = state.searchQuery,
                        onQueryChange = onSearchQueryChange
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(primaryColor.copy(alpha = 0.8f)) // Match top bar background
        ) {
            // Main Content Area in a "Sheet"
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column {
                    // Quick Stats Section
                    StatsRow(
                        total = state.totalProducts,
                        lowStock = state.lowStockCount
                    )

                    // Categories Filter
                    CategoryTabs(
                        categories = listOf("Tất cả") + state.products.map { it.categoryName }.distinct().filter { it.isNotEmpty() },
                        selectedCategory = state.selectedCategory,
                        onCategorySelected = onCategoryChange
                    )

                    // Product Grid/List
                    Box(modifier = Modifier.weight(1f)) {
                        val pullToRefreshState = rememberPullToRefreshState()

                        PullToRefreshBox(
                            isRefreshing = products.loadState.refresh is LoadState.Loading,
                            onRefresh = onRefresh,
                            state = pullToRefreshState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (products.itemCount == 0 && products.loadState.refresh !is LoadState.Loading) {
                                EmptyProductView(onAddProduct)
                            } else {
                                if (isGridView) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(products.itemCount) { index ->
                                            products[index]?.let { product ->
                                                ModernProductCard(
                                                    product = product,
                                                    onProductClick = onProductClick,
                                                    onEditClick = onEditProduct,
                                                    onDeleteClick = {
                                                        productToDelete = it
                                                        showDeleteConfirm = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(products.itemCount) { index ->
                                            products[index]?.let { product ->
                                                ModernProductListItem(
                                                    product = product,
                                                    onProductClick = onProductClick,
                                                    onEditClick = onEditProduct,
                                                    onDeleteClick = {
                                                        productToDelete = it
                                                        showDeleteConfirm = true
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Xác nhận xóa") },
                text = { Text("Bạn có chắc chắn muốn xóa '${productToDelete?.name}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            productToDelete?.let { onDeleteProduct(it.id) }
                            showDeleteConfirm = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Xóa") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy") }
                }
            )
        }
    }
}

@Composable
fun SearchCard(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Tìm kiếm tên, mã hàng...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
fun StatsRow(total: Int, lowStock: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatItem(
            label = "Tổng mặt hàng",
            value = total.toString(),
            icon = Icons.Default.Inventory2,
            containerColor = Color(0xFFE3F2FD),
            contentColor = Color(0xFF1976D2),
            modifier = Modifier.weight(1f)
        )
        StatItem(
            label = "Cảnh báo kho",
            value = lowStock.toString(),
            icon = Icons.Default.Warning,
            containerColor = Color(0xFFFFEBEE),
            contentColor = Color(0xFFD32F2F),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = contentColor)
                Text(label, style = MaterialTheme.typography.labelSmall, color = contentColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                modifier = Modifier.clickable { onCategorySelected(category) },
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(20.dp),
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ModernProductCard(
    product: Product,
    onProductClick: (Product) -> Unit,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit
) {
    val totalStock = product.variants.sumOf { it.stock }
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                if (!product.imageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                    }
                }
                
                // Stock Badge
                if (totalStock <= 10) {
                    Surface(
                        color = Color(0xFFFF5252),
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Sắp hết",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                // More Menu
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Chỉnh sửa") },
                            onClick = { showMenu = false; onEditClick(product) },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Xóa", color = Color.Red) },
                            onClick = { showMenu = false; onDeleteClick(product) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("vi-VN")).format(minPrice),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tồn kho: $totalStock", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    IconButton(onClick = { onEditClick(product) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ModernProductListItem(
    product: Product,
    onProductClick: (Product) -> Unit,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit
) {
    val totalStock = product.variants.sumOf { it.stock }
    val minPrice = product.variants.minOfOrNull { it.price } ?: 0.0

    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                if (!product.imageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = product.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = Color.LightGray)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(
                    java.text.NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("vi-VN")).format(minPrice),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text("Tồn kho: $totalStock", style = MaterialTheme.typography.labelMedium, color = if (totalStock <= 10) Color.Red else Color.Gray)
            }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.LightGray)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Chỉnh sửa") },
                        onClick = { showMenu = false; onEditClick(product) },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Xóa", color = Color.Red) },
                        onClick = { showMenu = false; onDeleteClick(product) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyProductView(onAddProduct: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Kho hàng trống", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Hãy thêm sản phẩm đầu tiên của bạn", color = Color.Gray)
        Button(
            onClick = onAddProduct,
            modifier = Modifier.padding(top = 24.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Thêm ngay")
        }
    }
}
