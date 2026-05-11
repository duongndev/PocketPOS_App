package com.duongnd.pocketposapp.feature.category

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // Pair(categoryId, isHardDelete)

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    if (state.showBottomSheet) {
        AddCategorySheet(
            category = state.selectedCategory,
            onDismiss = { viewModel.onShowBottomSheet(false) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quản lý thể loại", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadRemoteCategories() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onShowBottomSheet(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterChip(
                    label = "Tất cả",
                    isSelected = state.selectedStatus == null,
                    onClick = { viewModel.onStatusChange(null) }
                )
                StatusFilterChip(
                    label = "Đang hoạt động",
                    isSelected = state.selectedStatus == true,
                    onClick = { viewModel.onStatusChange(true) }
                )
                StatusFilterChip(
                    label = "Vô hiệu hóa",
                    isSelected = state.selectedStatus == false,
                    onClick = { viewModel.onStatusChange(false) }
                )
            }



            PullToRefreshBox(
                isRefreshing = state.isLoading && state.categories.isNotEmpty(),
                onRefresh = viewModel::loadRemoteCategories,
                modifier = Modifier.weight(1f)
            ) {
                if (state.isLoading && state.categories.isEmpty()) {
                    CategoryListShimmer()
                } else if (state.categories.isEmpty() && !state.isLoading) {
                    EmptyCategoryState(
                        isSearching = state.searchQuery.isNotEmpty(),
                        onAddClick = { viewModel.onShowBottomSheet(true) }
                    )
                } else {
                    val lazyListState = rememberLazyListState()
                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val layoutInfo = lazyListState.layoutInfo
                            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
                            lastVisibleItemIndex > (layoutInfo.totalItemsCount - 5)
                        }
                    }

                    LaunchedEffect(shouldLoadMore.value, state.hasNextPage, state.isPaginating) {
                        if (shouldLoadMore.value && state.hasNextPage && !state.isPaginating && !state.isLoading) {
                            viewModel.loadNextPage()
                        }
                    }

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.categories,
                            key = { it.id }
                        ) { category ->
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

                        if (state.isPaginating) {
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
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(8) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
fun SearchBar(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Tìm kiếm thể loại...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Close, contentDescription = null) }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun StatusFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun EmptyCategoryState(isSearching: Boolean, onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Default.Search else Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = if (isSearching) "Không tìm thấy" else "Chưa có thể loại", fontWeight = FontWeight.SemiBold)
        if (!isSearching) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onAddClick) { Text("Thêm mới ngay") }
        }
    }
}
