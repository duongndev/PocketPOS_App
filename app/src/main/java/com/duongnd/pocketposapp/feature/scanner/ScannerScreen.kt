package com.duongnd.pocketposapp.feature.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.components.AppDrawer
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScannerView
import com.duongnd.pocketposapp.feature.scanner.components.BarcodeScanningOverlay
import com.duongnd.pocketposapp.feature.scanner.components.ScannerBottomContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    navController: NavController,
    scanViewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isInspectionMode = LocalInspectionMode.current

    // Quan sát danh sách mã vạch từ ViewModel
    val scannedBarcodes by scanViewModel.scannedItems.collectAsState()
    val totalPrice = remember(scannedBarcodes) {
        scannedBarcodes.sumOf { it.price * it.count }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            isInspectionMode ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission && !isInspectionMode) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    val onBarcodeScanned: (String) -> Unit = remember {
        { barcode ->
            scanViewModel.searchProductByBarcode(barcode)
        }
    }

    AppDrawer(
        navController = navController,
        drawerState = drawerState,
        scope = scope
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        Scaffold(
            containerColor = Color.Black,
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
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                            Text(
                                "Quét mã sản phẩm",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                // Vùng Camera
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.2f)
                ) {

                    if (hasCameraPermission) {
                        BarcodeScannerView(
                            modifier = Modifier.fillMaxSize(),
                            onBarcodeScanned = onBarcodeScanned
                        )
                        BarcodeScanningOverlay(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "Yêu cầu quyền Camera",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Vui lòng cấp quyền để bắt đầu quét",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // Phần hiển thị kết quả quét bên dưới
                ScannerBottomContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.8f),
                    scannedItems = scannedBarcodes,
                    totalPrice = totalPrice,
                    onIncrease = { scanViewModel.increaseCount(it) },
                    onDecrease = { scanViewModel.decreaseCount(it) },
                    onReviewOrder = { navController.navigate(Routes.CHECKOUT) }
                )
            }
        }
    }
}
