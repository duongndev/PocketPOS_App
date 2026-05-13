package com.duongnd.pocketposapp.feature.category.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duongnd.pocketposapp.core.ui.components.AppOutlinedTextField
import com.duongnd.pocketposapp.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategorySheet(
    category: Category? = null,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember(category) { mutableStateOf(category?.name ?: "") }
    var description by remember(category) { mutableStateOf(category?.description ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (category == null) "Tạo thể loại mới" else "Chỉnh sửa thể loại",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            AppOutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên thể loại") },
                placeholder = { Text("Ví dụ: Đồ uống, Thức ăn nhanh...") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Label, null, tint = MaterialTheme.colorScheme.primary) }
            )

            AppOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                placeholder = { Text("Nhập mô tả ngắn gọn về thể loại này...") },
                leadingIcon = { Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = false,
                minLines = 3
            )

            Button(
                onClick = { onSave(name, description) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = if (category == null) "LƯU THỂ LOẠI" else "CẬP NHẬT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
