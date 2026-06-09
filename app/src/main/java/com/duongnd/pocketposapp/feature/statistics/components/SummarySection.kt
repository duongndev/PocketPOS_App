package com.duongnd.pocketposapp.feature.statistics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duongnd.pocketposapp.data.remote.dto.statistics.Summary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SummarySection(summary: Summary?) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Tổng quan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 2
        ) {
            SummaryCard(
                title = "Doanh thu",
                value = summary?.revenue?.let { currencyFormatter.format(it) } ?: "0đ",
                icon = Icons.Default.AttachMoney,
                trend = "",
                trendUp = true,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            SummaryCard(
                title = "Lợi nhuận",
                value = summary?.profit?.let { currencyFormatter.format(it) } ?: "0đ",
                icon = Icons.Default.MonetizationOn,
                trend = "",
                trendUp = true,
                modifier = Modifier.weight(1f),
                containerColor = Color(0xFFE8F5E9) // Light green
            )
            SummaryCard(
                title = "Đơn hàng",
                value = summary?.orders?.toString() ?: "0",
                icon = Icons.Default.ShoppingCart,
                trend = "",
                trendUp = true,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            SummaryCard(
                title = "Giá trị TB",
                value = summary?.averageOrderValue?.let { currencyFormatter.format(it) } ?: "0đ",
                icon = Icons.AutoMirrored.Filled.Assignment,
                trend = "",
                trendUp = true,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
            SummaryCard(
                title = "Sản phẩm",
                value = summary?.totalProducts?.toString() ?: "0",
                icon = Icons.Default.Inventory,
                trend = "",
                trendUp = true,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
