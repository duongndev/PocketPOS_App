package com.duongnd.pocketposapp.feature.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton
import com.duongnd.pocketposapp.feature.product.components.ProductFormContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController,
    productId: String,
    viewModel: EditProductViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) navController.popBackStack()
    }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = { 
                        Text(
                            if (state.name.isBlank()) "Chỉnh sửa sản phẩm" else state.name, 
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Thông tin", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(if (selectedTab == 0) Icons.Filled.Description else Icons.Outlined.Description, null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Kho hàng", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        icon = { Icon(if (selectedTab == 1) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2, null) }
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                PrimaryButton(
                    text = "LƯU THAY ĐỔI",
                    onClick = { viewModel.saveProduct() },
                    isLoading = state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ProductFormContent(
                state = state,
                currentStep = selectedTab,
                onNameChange = viewModel::onNameChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onCategorySelect = viewModel::onCategorySelect,
                onImageChange = viewModel::onImageChange,
                onHasVariantsChange = viewModel::onHasVariantsChange,
                onAddAttribute = viewModel::addAttribute,
                onUpdateAttributeName = viewModel::updateAttributeName,
                onAddAttributeValue = viewModel::addAttributeValue,
                onRemoveAttribute = viewModel::removeAttribute,
                onRemoveAttributeValue = viewModel::removeAttributeValue,
                onUpdateVariant = viewModel::updateVariant,
                onScanBarcode = { /* Logic scan barcode */ },
                onCreateCategory = viewModel::createCategory
            )
            
            if (state.isLoading && state.name.isBlank()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa sản phẩm?") },
            text = { Text("Bạn có chắc chắn muốn xóa sản phẩm này? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        viewModel.deleteProduct()
                        showDeleteDialog = false 
                    }
                ) {
                    Text("XÓA NGAY", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("HỦY", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }
}
