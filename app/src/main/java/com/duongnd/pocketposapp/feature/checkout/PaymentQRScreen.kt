package com.duongnd.pocketposapp.feature.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.utils.formatPrice
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentQRScreen(
    navController: NavController,
    viewModel: CheckoutViewModel,
    orderId: String,
    totalPrice: Double,
    qrUrl: String
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isPaymentSuccess by viewModel.isPaymentSuccess.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    val bankName = viewModel.store?.bankName ?: "N/A"
    val accountNo = viewModel.store?.bankAccountNumber ?: "N/A"
    val accountName = viewModel.store?.bankAccountName ?: "N/A"

    LaunchedEffect(orderId) {
        viewModel.connectSocket(orderId)
    }

    LaunchedEffect(isPaymentSuccess) {
        if (isPaymentSuccess) {
            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_LONG).show()
            delay(2000) // Để người dùng thấy trạng thái thành công
            navController.navigate(Routes.paymentSuccess()) {
                popUpTo(Routes.SCANNER)
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Thanh toán chuyển khoản", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Giao dịch được bảo mật tuyệt đối",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    
                    Button(
                        onClick = {
                            navController.popBackStack(Routes.SCANNER, false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !isLoading && !isPaymentSuccess,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaymentSuccess) Color(0xFF4CAF50) else Color(0xFF506490)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else if (isPaymentSuccess) {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "THANH TOÁN THÀNH CÔNG",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "XÁC NHẬN ĐÃ CHUYỂN KHOẢN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Amount Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Tổng số tiền cần trả",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatPrice(totalPrice.toLong())} đ",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF506490),
                        fontWeight = FontWeight.ExtraBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Surface(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(totalPrice.toLong().toString()))
                            Toast.makeText(context, "Đã sao chép số tiền", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF506490).copy(alpha = 0.05f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(14.dp), tint = Color(0xFF506490))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sao chép", style = MaterialTheme.typography.labelMedium, color = Color(0xFF506490))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // QR Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = qrUrl,
                            contentDescription = "Payment QR Code",
                            modifier = Modifier.size(260.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                        Text(
                            " THÔNG TIN TÀI KHOẢN ",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    BankDetailItem(
                        label = "Ngân hàng",
                        value = bankName
                    )
                    BankDetailItem(
                        label = "Số tài khoản",
                        value = accountNo,
                        showCopy = true,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(accountNo))
                            Toast.makeText(context, "Đã sao chép số tài khoản", Toast.LENGTH_SHORT).show()
                        }
                    )
                    BankDetailItem(
                        label = "Chủ tài khoản",
                        value = accountName
                    )
                    BankDetailItem(
                        label = "Nội dung",
                        value = "Thanh toan don hang",
                        isItalic = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "Lưu ý: Sau khi chuyển khoản thành công, vui lòng nhấn nút bên dưới để hệ thống cập nhật đơn hàng.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BankDetailItem(
    label: String,
    value: String,
    showCopy: Boolean = false,
    onCopy: (() -> Unit)? = null,
    isItalic: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = if (isItalic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                    fontSize = 17.sp
                ),
                color = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
            
            if (showCopy) {
                IconButton(
                    onClick = { onCopy?.invoke() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = Color(0xFF506490),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
