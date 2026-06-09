package com.duongnd.pocketposapp.feature.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    onOpenDrawer: () -> Unit,
    scanViewModel: ScanViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isInspectionMode = LocalInspectionMode.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Quan sát danh sách mã vạch từ ViewModel
    val scannedItems by scanViewModel.scannedItems.collectAsState()
    val error by scanViewModel.error.collectAsState()
    val totalPrice = remember(scannedItems) {
        scannedItems.sumOf { it.price * it.count }
    }

    // Hiển thị Dialog lỗi nếu có
    if (error != null) {
        AlertDialog(
            onDismissRequest = { scanViewModel.clearError() },
            title = { Text("Lỗi quét mã") },
            text = { Text(error ?: "") },
            confirmButton = {
                TextButton(onClick = { scanViewModel.clearError() }) {
                    Text("Đóng")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Xử lý quyền truy cập Camera
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

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            ScannerTopBar(
                screenWidth = screenWidth,
                onMenuClick = onOpenDrawer
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
//                    .systemBarsPadding()
        ) {
            // Vùng Quét Camera - Điều chỉnh weight dựa trên chiều cao màn hình
            val cameraWeight = if (screenHeight < 640.dp) 1.0f else 1.2f
            val contentWeight = if (screenHeight < 640.dp) 2.0f else 1.8f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(cameraWeight),
                contentAlignment = Alignment.Center
            ) {
                if (hasCameraPermission) {
                    BarcodeScannerView(
                        modifier = Modifier.fillMaxSize(),
                        onBarcodeScanned = { barcode ->
                            scanViewModel.searchProductByBarcode(barcode)
                        }
                    )
                    BarcodeScanningOverlay(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    PermissionDeniedPlaceholder(screenWidth)
                }
            }

            // Phần hiển thị danh sách sản phẩm đã quét và tổng tiền
            ScannerBottomContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(contentWeight),
                scannedItems = scannedItems,
                totalPrice = totalPrice,
                onIncrease = { scanViewModel.increaseCount(it) },
                onDecrease = { scanViewModel.decreaseCount(it) },
                onRemove = { scanViewModel.removeItem(it) },
                onReviewOrder = { navController.navigate(Routes.CHECKOUT) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerTopBar(
    screenWidth: Dp,
    onMenuClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val titleFontSize = if (screenWidth < 360.dp) 18.sp else 20.sp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.9f))
                )
            )
            .statusBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Quét mã sản phẩm",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = titleFontSize
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun PermissionDeniedPlaceholder(screenWidth: Dp) {
    val scaleFactor = (screenWidth.value / 360f).coerceIn(0.8f, 1.2f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding((32 * scaleFactor).dp)
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size((64 * scaleFactor).dp)
        )
        Spacer(modifier = Modifier.height((16 * scaleFactor).dp))
        Text(
            text = "Yêu cầu quyền Camera",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = (18 * scaleFactor).sp
            ),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height((8 * scaleFactor).dp))
        Text(
            text = "Vui lòng cấp quyền trong cài đặt để bắt đầu quét mã sản phẩm.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = (14 * scaleFactor).sp
            ),
            textAlign = TextAlign.Center
        )
    }
}
