package com.duongnd.pocketposapp.feature.category

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.duongnd.pocketposapp.feature.category.components.AddCategorySheet
import com.duongnd.pocketposapp.feature.category.components.CategoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val categories = viewModel.categoriesPagingData.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    if (state.showBottomSheet) {
        AddCategorySheet(
            category = state.selectedCategory,
            onDismiss = { viewModel.onShowBottomSheet(show = false) },
            onSave = { name, desc -> viewModel.saveCategory(name, description = desc) }
        )
    }

    if (showDeleteDialog != null) {
        val (categoryId, isHardDelete) = showDeleteDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(if (isHardDelete) "Xác nhận xóa vĩnh viễn" else "Xác nhận xóa") },
            text = {
                Text(
                    if (isHardDelete) "Dữ liệu thể loại này sẽ bị xóa vĩnh viễn khỏi hệ thống. Thao tác này không thể hoàn tác!"
                    else "Dữ liệu thể loại này sẽ bị chuyển vào thùng rác. Bạn có thể khôi phục sau này."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(categoryId, isHardDelete)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(if (isHardDelete) "Xóa vĩnh viễn" else "Xóa") }
            },
            dismissButton = { OutlinedButton(onClick = { showDeleteDialog = null }) { Text("Hủy") } }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        Text(
                            "Quản lý thể loại",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        )
                        IconButton(onClick = { categories.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    }

                    // Search Bar
                    CategorySearchCard(
                        query = state.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onShowBottomSheet(true) },
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Thêm mới", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(primaryColor.copy(alpha = 0.8f))
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF5F7F9),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column {
                    // Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 24.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModernStatusChip(
                            label = "Tất cả",
                            isSelected = state.selectedStatus == null,
                            onClick = { viewModel.onStatusChange(null) }
                        )
                        ModernStatusChip(
                            label = "Hoạt động",
                            isSelected = state.selectedStatus == true,
                            onClick = { viewModel.onStatusChange(true) }
                        )
                        ModernStatusChip(
                            label = "Lưu trữ",
                            isSelected = state.selectedStatus == false,
                            onClick = { viewModel.onStatusChange(false) }
                        )
                    }

                    PullToRefreshBox(
                        isRefreshing = categories.loadState.refresh is LoadState.Loading,
                        onRefresh = { categories.refresh() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (categories.loadState.refresh is LoadState.Loading && categories.itemCount == 0) {
                            CategoryListShimmer()
                        } else if (categories.itemCount == 0 && categories.loadState.refresh !is LoadState.Loading) {
                            EmptyCategoryState(
                                isSearching = state.searchQuery.isNotEmpty(),
                                onAddClick = { viewModel.onShowBottomSheet(true) }
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    count = categories.itemCount,
                                    key = { index -> categories[index]?.id ?: index }
                                ) { index ->
                                    categories[index]?.let { category ->
                                        CategoryItem(
                                            category = category,
                                            isRevealed = state.revealedCategoryId == category.id,
                                            onExpanded = { viewModel.onRevealedCategoryChange(category.id) },
                                            onCollapsed = {
                                                if (state.revealedCategoryId == category.id) {
                                                    viewModel.onRevealedCategoryChange(null)
                                                }
                                            },
                                            onEditClick = { viewModel.onShowBottomSheet(true, category) },
                                            onDeleteClick = { showDeleteDialog = category.id to false },
                                            onHardDeleteClick = { showDeleteDialog = category.id to true }
                                        )
                                    }
                                }

                                if (categories.loadState.append is LoadState.Loading) {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                        }
                                    }
                                }
                                
                                item { Spacer(modifier = Modifier.height(80.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySearchCard(query: String, onQueryChange: (String) -> Unit) {
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
            placeholder = { Text("Tìm kiếm thể loại...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, null, tint = Color.Gray)
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
}

@Composable
fun ModernStatusChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isSelected) Color.White else Color.Gray
    val borderStroke = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE9ECEF))

    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = borderStroke,
        modifier = Modifier.height(40.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun CategoryListShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.6f),
            Color.White.copy(alpha = 0.2f),
            Color.White.copy(alpha = 0.6f),
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(8) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(brush))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth(0.6f).height(20.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth(0.4f).height(14.dp).clip(RoundedCornerShape(4.dp)).background(brush))
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCategoryState(isSearching: Boolean, onAddClick: () -> Unit) {
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
                imageVector = if (isSearching) Icons.Default.SearchOff else Icons.Default.Category,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (isSearching) "Không tìm thấy thể loại nào" else "Danh sách thể loại trống",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isSearching) "Hãy thử tìm kiếm với từ khóa khác" else "Bắt đầu bằng cách thêm thể loại sản phẩm đầu tiên của bạn",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (!isSearching) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm thể loại", fontWeight = FontWeight.Bold)
            }
        }
    }
}
