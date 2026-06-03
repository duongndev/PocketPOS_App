package com.duongnd.pocketposapp.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.duongnd.pocketposapp.core.ui.theme.PocketPOSAppTheme
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.feature.checkout.components.CheckoutBottomContent
import com.duongnd.pocketposapp.feature.checkout.components.CheckoutTopBar
import com.duongnd.pocketposapp.feature.checkout.components.PaymentMethodSelection
import com.duongnd.pocketposapp.feature.checkout.components.ReceiptCard
import com.duongnd.pocketposapp.feature.scanner.ScanViewModel
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CheckoutScreen(
    navController: NavController, viewModel: ScanViewModel
) {
    val items by viewModel.scannedItems.collectAsState()
    CheckoutContent(
        items = items,
        store = viewModel.store,
        onBackClick = { navController.popBackStack() },
        onConfirmPayment = { /* TODO: Process Payment */ })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutContent(
    items: List<ScannedItem>,
    store: StoreDTO?,
    onBackClick: () -> Unit,
    onConfirmPayment: (String) -> Unit,
    initialPaymentMethod: String? = null
) {
    val totalPrice = items.sumOf { it.price * it.count }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(initialPaymentMethod) }
    MaterialTheme.colorScheme.primary
    val currentDate =
        remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()) }

    Scaffold(topBar = {
        CheckoutTopBar(onBackClick = onBackClick)
    }, bottomBar = {
        CheckoutBottomContent(
            totalPrice = totalPrice,
            selectedPaymentMethod = selectedPaymentMethod,
            onConfirmPayment = onConfirmPayment
        )
    }) { paddingValues ->
        val scrollState = rememberScrollState()

        // Auto-scroll to bottom when QR code appears
        LaunchedEffect(selectedPaymentMethod) {
            if (selectedPaymentMethod == "QR") {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

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

            // Dynamic spacer to push content up when QR is shown in bottom bar
            // Increased height to ensure everything scrolls above the tall bottom sheet
            if (selectedPaymentMethod == "QR") {
                Spacer(modifier = Modifier.height(400.dp))
            } else {
                Spacer(modifier = Modifier.height(32.dp))
            }
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
            onConfirmPayment = {},
            initialPaymentMethod = "QR"
        )
    }
}
