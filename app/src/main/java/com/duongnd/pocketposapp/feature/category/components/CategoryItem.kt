package com.duongnd.pocketposapp.feature.category.components

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.duongnd.pocketposapp.domain.model.Category
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeState { Expanded, Collapsed }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryItem(
    category: Category,
    isRevealed: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val actionWidth = 80.dp

    val anchors = remember(density) {
        DraggableAnchors {
            SwipeState.Collapsed at 0f
            SwipeState.Expanded at -with(density) { actionWidth.toPx() }
        }
    }

    val decayAnimationSpec = remember { exponentialDecay<Float>() }
    val state = remember(anchors) {
        AnchoredDraggableState(
            initialValue = if (isRevealed) SwipeState.Expanded else SwipeState.Collapsed,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec
        )
    }

    // Đồng bộ với trạng thái bên ngoài (ví dụ: đóng khi mục khác mở)
    LaunchedEffect(isRevealed) {
        if (!isRevealed && state.currentValue == SwipeState.Expanded) {
            state.animateTo(SwipeState.Collapsed)
        }
    }

    // Thông báo cho cha khi trạng thái thay đổi
    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SwipeState.Expanded) {
            onExpanded()
        } else if (state.currentValue == SwipeState.Collapsed) {
            onCollapsed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        val currentOffset = if (state.offset.isNaN()) 0f else state.offset

        // Giao diện nút xóa phía dưới (Actions Behind)
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = 2.dp)
                .background(Color.Red, shape = RoundedCornerShape(12.dp))
                .clickable {
                    onDeleteClick()
                    scope.launch { state.animateTo(SwipeState.Collapsed) }
                },
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa",
                    tint = Color.White
                )
            }
        }

        // Nội dung chính phía trên (Content Front)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        x = currentOffset.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal
                )
                .clickable {
                    if (state.currentValue == SwipeState.Expanded) {
                        scope.launch { state.animateTo(SwipeState.Collapsed) }
                    } else {
                        // Có thể thêm onClick cho item ở đây nếu cần
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (category.isActive) {
                            Text(
                                "Hoạt động",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                    category.description?.let {
                        if (it.isNotEmpty()) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }

                IconButton(onClick = {
                    scope.launch { state.animateTo(SwipeState.Collapsed) }
                    onEditClick()
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
