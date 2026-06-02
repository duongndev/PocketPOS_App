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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    onDeleteClick: () -> Unit,
    onClick: () -> Unit = {}
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

    LaunchedEffect(isRevealed) {
        if (!isRevealed && state.currentValue == SwipeState.Expanded) {
            state.animateTo(SwipeState.Collapsed)
        } else if (isRevealed && state.currentValue == SwipeState.Collapsed) {
            state.animateTo(SwipeState.Expanded)
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
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFF8F9FA)),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Delete Action
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                    .background(Color(0xFFEF5350))
                    .clickable {
                        onDeleteClick()
                        scope.launch { state.animateTo(SwipeState.Collapsed) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Delete, "Xóa", tint = Color.White)
                    Text("Xóa", style = MaterialTheme.typography.labelSmall, color = Color.White)
                }
            }
        }

        // Foreground Content
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(x = currentOffset.roundToInt(), y = 0) }
                .anchoredDraggable(state = state, orientation = Orientation.Horizontal)
                .clickable { onClick() },
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 2.dp,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, Color(0xFFF1F3F5))
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Indicator bar
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .clip(CircleShape)
                        .background(if (category.isActive) Color(0xFF4CAF50) else Color(0xFFBDBDBD))
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Category,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D3436)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (category.isActive) "Hoạt động" else "Đã xóa",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (category.isActive) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                    
                    if (!category.description.isNullOrEmpty()) {
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        scope.launch { state.animateTo(SwipeState.Collapsed) }
                        onEditClick()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF8F9FA))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = Color(0xFF636E72),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
