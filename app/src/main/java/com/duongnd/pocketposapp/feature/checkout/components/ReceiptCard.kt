package com.duongnd.pocketposapp.feature.checkout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.duongnd.pocketposapp.core.utils.formatPrice
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import kotlin.collections.forEach

@Composable
fun ReceiptCard(
    items: List<ScannedItem>,
    totalPrice: Long,
    currentDate: String,
    store: StoreDTO?,
    modifier: Modifier = Modifier,
    orderNumber: String? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val storeName = store?.storeName ?: "POCKET POS"
    val storeAddress = store?.address ?: "123 ABC, Hà Nội"
    val storePhone = store?.phoneNumber ?: "0123456789"

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Store Info
            Text(
                text = storeName.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = storeAddress,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "ĐT: $storePhone",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Order Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (orderNumber != null) {
                    Text(
                        text = "Số: #$orderNumber",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Text(currentDate, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items Header
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Sản phẩm",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "SL",
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "T.Tiền",
                    modifier = Modifier.width(80.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items List
            items.forEach { item ->
                ReceiptItemRow(item)
            }

            Spacer(modifier = Modifier.height(16.dp))
            DashedDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TỔNG CỘNG",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    formatPrice(totalPrice) + " đ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Cảm ơn quý khách. Hẹn gặp lại!",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.Gray
            )
        }
    }
}
