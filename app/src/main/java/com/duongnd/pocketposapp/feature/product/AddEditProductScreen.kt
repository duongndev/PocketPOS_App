package com.duongnd.pocketposapp.feature.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.data.remote.dto.CategoryDTO
import com.duongnd.pocketposapp.data.remote.dto.ProductDTO
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScannerView
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScanningOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    navController: NavController,
    productId: String? = null // null means adding, otherwise editing
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var costPrice by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var showScanner by remember { mutableStateOf(false) }

    // Mock category list
    val categories = remember {
        listOf(
            CategoryDTO("1", "Sữa & Đồ uống", "", true, "", ""),
            CategoryDTO("2", "Bánh kẹo", "", true, "", ""),
            CategoryDTO("3", "Đồ uống", "", true, "", "")
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryName by remember { mutableStateOf("Chọn danh mục") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (productId == null) "Thêm sản phẩm" else "Sửa sản phẩm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Logic lưu */ }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Mã vạch") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    FilledIconButton(
                        onClick = { showScanner = true },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan")
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Danh mục") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    categoryId = category._id
                                    selectedCategoryName = category.name
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = costPrice,
                        onValueChange = { costPrice = it },
                        label = { Text("Giá vốn") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Giá bán") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Số lượng tồn kho") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Trạng thái kinh doanh", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Logic lưu */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (productId == null) "Thêm sản phẩm" else "Cập nhật sản phẩm")
                }
            }

            if (showScanner) {
                AlertDialog(
                    onDismissRequest = { showScanner = false },
                    modifier = Modifier.fillMaxWidth().height(400.dp),
                    properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.weight(1f)) {
                                BarcodeScannerView(
                                    modifier = Modifier.fillMaxSize(),
                                    onBarcodeScanned = { result ->
                                        barcode = result
                                        showScanner = false
                                    }
                                )
                                BarcodeScanningOverlay(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxSize()
//                                        .aspectRatio(1.6f)
                                )
                            }
                            TextButton(
                                onClick = { showScanner = false },
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
                            ) {
                                Text("Đóng")
                            }
                        }
                    }
                }
            }
        }
    }
}
