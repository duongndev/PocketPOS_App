package com.duongnd.pocketposapp.feature.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.core.ui.components.PrimaryButton
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreInfoScreen(
    navController: NavController,
    from: String? = null,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    if (state.showResultDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissResultDialog() },
            icon = {
                Icon(
                    imageVector = if (state.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (state.isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = if (state.isSuccess) "Thành công" else "Thất bại",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = state.resultMessage ?: "",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onDismissResultDialog()
                        if (state.isSuccess && (from == "splash" || from == "login")) {
                            navController.navigate(Routes.SCANNER) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Đóng")
                }
            }
        )
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

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
                value = state.storeName,
                onValueChange = { viewModel.onStoreNameChange(it) },
                label = { Text("Tên cửa hàng") },
                leadingIcon = { Icon(Icons.Default.Store, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.onAddressChange(it) },
                label = { Text("Địa chỉ") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = { Text("Số điện thoại") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
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
                value = state.bankName,
                onValueChange = { viewModel.onBankNameChange(it) },
                label = { Text("Tên ngân hàng") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = state.bankAccountNumber,
                onValueChange = { viewModel.onBankAccountNumberChange(it) },
                label = { Text("Số tài khoản") },
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppOutlinedTextField(
                value = state.bankAccountName,
                onValueChange = { viewModel.onBankAccountNameChange(it) },
                label = { Text("Tên chủ tài khoản") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                PrimaryButton(
                    text = "LƯU THÔNG TIN",
                    onClick = { viewModel.updateStoreInfo() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
