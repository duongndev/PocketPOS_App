package com.duongnd.pocketposapp.feature.category.components

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onDeleteClick: () -> Unit,
    onHardDeleteClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val actionWidth = 160.dp

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

    LaunchedEffect(isRevealed) {
        if (!isRevealed && state.currentValue == SwipeState.Expanded) {
            state.animateTo(SwipeState.Collapsed)
        }
    }

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

        // Background Actions
        Row(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF1F3F5)),
            horizontalArrangement = Arrangement.End
        ) {
            // Soft Delete Action
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFFFEBEE))
                    .clickable {
                        onDeleteClick()
                        scope.launch { state.animateTo(SwipeState.Collapsed) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Delete, "Lưu trữ", tint = Color(0xFFD32F2F))
            }

            // Hard Delete Action
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFD32F2F))
                    .clickable {
                        onHardDeleteClick()
                        scope.launch { state.animateTo(SwipeState.Collapsed) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DeleteForever, "Xóa vĩnh viễn", tint = Color.White)
            }
        }

        // Foreground Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = currentOffset.roundToInt(), y = 0) }
                .anchoredDraggable(state = state, orientation = Orientation.Horizontal),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Icon/Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Category,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    category.description?.let {
                        if (it.isNotEmpty()) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Status Badge
                    Surface(
                        color = if (category.isActive) Color(0xFFE8F5E9) else Color(0xFFF1F3F5),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = if (category.isActive) "Đang hoạt động" else "Đã lưu trữ",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (category.isActive) Color(0xFF2E7D32) else Color.Gray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        scope.launch { state.animateTo(SwipeState.Collapsed) }
                        onEditClick()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
