package com.duongnd.pocketposapp.feature.checkout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PaymentMethodSelection(
    selectedPaymentMethod: String?,
    onPaymentMethodSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(modifier = modifier) {
        Text(
            "Phương thức thanh toán",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        PaymentMethodItem(
            title = "Tiền mặt",
            icon = Icons.Default.Payments,
            isSelected = selectedPaymentMethod == "CASH",
            onClick = { onPaymentMethodSelect("CASH") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        PaymentMethodItem(
            title = "Chuyển khoản / QR Code",
            icon = Icons.Default.QrCode2,
            isSelected = selectedPaymentMethod == "QR",
            onClick = { onPaymentMethodSelect("QR") }
        )
    }
}
