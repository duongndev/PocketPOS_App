package com.duongnd.pocketposapp.feature.scanner.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeScanningOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Cố định tỷ lệ khung quét: Rộng 80%, Cao 25% màn hình
        val frameWidth = width * 0.8f
        val frameHeight = height * 0.25f
        val left = (width - frameWidth) / 2f
        val top = (height - frameHeight) / 2f
        val right = left + frameWidth
        val bottom = top + frameHeight

        // 1. Vẽ Mask (Phần tối xung quanh) với lỗ thủng ở giữa
        val maskPath = Path().apply {
            addRect(Rect(0f, 0f, width, height))
            addRect(Rect(left, top, right, bottom))
            fillType = PathFillType.EvenOdd
        }
        drawPath(path = maskPath, color = Color.Black.copy(alpha = 0.6f))

        // 2. Vẽ các góc (Frame Corners) màu Cyan
        val strokeWidth = 3.dp.toPx()
        val cornerSize = 25.dp.toPx()
        val cornerColor = Color(0xFF00E5FF)

        // Top Left
        drawLine(cornerColor, Offset(left, top), Offset(left + cornerSize, top), strokeWidth)
        drawLine(cornerColor, Offset(left, top), Offset(left, top + cornerSize), strokeWidth)

        // Top Right
        drawLine(cornerColor, Offset(right, top), Offset(right - cornerSize, top), strokeWidth)
        drawLine(cornerColor, Offset(right, top), Offset(right, top + cornerSize), strokeWidth)

        // Bottom Left
        drawLine(cornerColor, Offset(left, bottom), Offset(left + cornerSize, bottom), strokeWidth)
        drawLine(cornerColor, Offset(left, bottom), Offset(left, bottom - cornerSize), strokeWidth)

        // Bottom Right
        drawLine(cornerColor, Offset(right, bottom), Offset(right - cornerSize, bottom), strokeWidth)
        drawLine(cornerColor, Offset(right, bottom), Offset(right, bottom - cornerSize), strokeWidth)

        // 3. Vẽ tia Laser chạy lên xuống
        val laserY = top + (frameHeight * laserPosition)
        val laserBrush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.Red, Color.Transparent),
            startX = left,
            endX = right
        )

        drawLine(
            brush = laserBrush,
            start = Offset(left, laserY),
            end = Offset(right, laserY),
            strokeWidth = 2.dp.toPx()
        )
    }
}
