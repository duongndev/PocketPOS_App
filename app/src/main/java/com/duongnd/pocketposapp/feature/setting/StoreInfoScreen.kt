package com.duongnd.pocketposapp.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreInfoScreen(navController: NavController) {
    var storeName by remember { mutableStateOf("PocketPOS Store") }
    var address by remember { mutableStateOf("123 Đường ABC, Hà Nội") }
    var phone by remember { mutableStateOf("0987654321") }
    var description by remember { mutableStateOf("Chuyên cung cấp giải pháp bán hàng thông minh.") }
    
    var bankName by remember { mutableStateOf("") }
    var bankAccountNumber by remember { mutableStateOf("") }
    var bankAccountName by remember { mutableStateOf("") }

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
                        "Thông tin cửa hàng",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = primaryColor.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppOutlinedTextField(
                value = storeName,
                onValueChange = { storeName = it },
                label = { Text("Tên cửa hàng") },
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Địa chỉ") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                singleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Bank Information Section
            Text(
                text = "Thông tin ngân hàng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Tên ngân hàng") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = bankAccountNumber,
                onValueChange = { bankAccountNumber = it },
                label = { Text("Số tài khoản") },
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = bankAccountName,
                onValueChange = { bankAccountName = it },
                label = { Text("Tên chủ tài khoản") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = "LƯU THÔNG TIN",
                onClick = { /* TODO: Save Store Info */ },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
