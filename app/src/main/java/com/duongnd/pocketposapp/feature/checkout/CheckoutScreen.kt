package com.duongnd.pocketposapp.feature.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.navigation.Routes
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.core.ui.theme.PocketPOSAppTheme
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.feature.checkout.components.CheckoutBottomContent
import com.duongnd.pocketposapp.feature.checkout.components.CheckoutTopBar
import com.duongnd.pocketposapp.feature.checkout.components.PaymentMethodSelection
import com.duongnd.pocketposapp.feature.checkout.components.ReceiptCard
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CheckoutScreen(
    navController: NavController, viewModel: CheckoutViewModel
) {
    val items by viewModel.scannedItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.orderCreated.collectLatest { response ->
            when (response.paymentMethod.lowercase()) {
                "cash" -> {
                    navController.navigate(Routes.paymentSuccess()) {
                        popUpTo(Routes.SCANNER)
                    }
                }
                "bank_transfer" -> {
                    val totalPrice = items.sumOf { it.price * it.count }
                    navController.navigate(Routes.paymentQr(response.orderId, totalPrice, response.qrUrl ?: ""))
                }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    CheckoutContent(
        items = items,
        store = viewModel.store,
        isLoading = isLoading,
        onBackClick = { navController.popBackStack() },
        onConfirmPayment = { method, note ->
            viewModel.createOrder(method.lowercase(), note)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    items: List<ScannedItem>,
    store: StoreDTO?,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onConfirmPayment: (String, String) -> Unit,
    initialPaymentMethod: String? = null
) {
    val totalPrice = items.sumOf { it.price * it.count }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(initialPaymentMethod) }
    val currentDate =
        remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()) }

    Scaffold(topBar = {
        CheckoutTopBar(onBackClick = onBackClick)
    }, bottomBar = {
        CheckoutBottomContent(
            totalPrice = totalPrice,
            selectedPaymentMethod = selectedPaymentMethod,
            isLoading = isLoading,
            onConfirmPayment = { method ->
                val paymentNote = when (method) {
                    "cash" -> "Thanh toán tiền mặt"
                    "bank_transfer" -> "Thanh toán chuyển khoản"
                    else -> ""
                }
                onConfirmPayment(method, paymentNote)
            }
        )
    }) { paddingValues ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            ReceiptCard(
                items = items,
                totalPrice = totalPrice.toLong(),
                currentDate = currentDate,
                store = store
            )

            Spacer(modifier = Modifier.height(32.dp))

            PaymentMethodSelection(
                selectedPaymentMethod = selectedPaymentMethod,
                onPaymentMethodSelect = { selectedPaymentMethod = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CheckoutScreenQRPreview() {
    val sampleItems = listOf(
        ScannedItem("1", "123456", "Sữa tươi Vinamilk", 12000.0, 2),
        ScannedItem("2", "789012", "Bánh mì gối", 15000.0, 1)
    )
    PocketPOSAppTheme {
        CheckoutContent(
            items = sampleItems,
            store = null,
            onBackClick = {},
            onConfirmPayment = { _, _ -> },
            initialPaymentMethod = "bank_transfer"
        )
    }
}
