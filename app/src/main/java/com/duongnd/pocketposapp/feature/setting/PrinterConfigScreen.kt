package com.duongnd.pocketposapp.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrinterConfigScreen(navController: NavController) {
    var isBluetoothEnabled by remember { mutableStateOf(false) }
    var isWifiEnabled by remember { mutableStateOf(false) }
    var selectedPrinter by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Scaffold(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        "Cấu hình máy in",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Kết nối máy in hóa đơn để bắt đầu in hóa đơn bán hàng.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bluetooth Connection
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null, tint = Color(0xFF2196F3))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Bluetooth",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isBluetoothEnabled,
                        onCheckedChange = { isBluetoothEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wifi Connection
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Wi-Fi / LAN",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Switch(
                        checked = isWifiEnabled,
                        onCheckedChange = { isWifiEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Máy in đã tìm thấy",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isBluetoothEnabled && !isWifiEnabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bật kết nối để tìm kiếm máy in",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // Mock List of Printers
                val printers = listOf("Xprinter XP-N160I", "Epson TM-T82III", "Sunmi V2 Printer")
                printers.forEach { printer ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        onClick = { selectedPrinter = printer }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = printer,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (selectedPrinter == printer) {
                                Icon(Icons.Default.Bluetooth, contentDescription = "Connected", tint = primaryColor)
                            }
                        }
                    }
                }
            }
        }
    }
}
