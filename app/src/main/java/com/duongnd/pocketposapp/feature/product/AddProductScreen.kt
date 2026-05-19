package com.duongnd.pocketposapp.feature.product

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton
import com.duongnd.pocketposapp.feature.product.components.ProductFormContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: AddProductViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) navController.popBackStack()
    }

    LaunchedEffect(state.error) {
        state.error?.let { 
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            ) 
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = { 
                        Text(
                            "Tạo sản phẩm", 
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.headlineSmall
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            if (currentStep > 0) currentStep-- 
                            else navController.popBackStack() 
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                
                // Step Indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem(step = 0, currentStep = currentStep, title = "Cơ bản")
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                    StepItem(step = 1, currentStep = currentStep, title = "Kho hàng")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentStep == 0) {
                        PrimaryButton(
                            text = "TIẾP TỤC",
                            onClick = { 
                                if (state.name.isNotBlank() && state.selectedCategoryId != null) {
                                    currentStep = 1
                                } else {
                                    viewModel.saveProduct() // Trình kích hoạt hiển thị lỗi
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        OutlinedButton(
                            onClick = { currentStep = 0 },
                            modifier = Modifier.height(56.dp).weight(0.4f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("QUAY LẠI")
                        }
                        PrimaryButton(
                            text = "HOÀN TẤT",
                            onClick = { viewModel.saveProduct() },
                            isLoading = state.isLoading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
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
                currentStep = currentStep,
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
        }
    }
}

@Composable
fun StepItem(step: Int, currentStep: Int, title: String) {
    val isActive = step == currentStep
    val isCompleted = step < currentStep
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = if (isActive || isCompleted) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9),
            border = if (!isActive && !isCompleted) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = Color.White)
                } else {
                    Text(
                        "${step + 1}", 
                        color = if (isActive) Color.White else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            title, 
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
