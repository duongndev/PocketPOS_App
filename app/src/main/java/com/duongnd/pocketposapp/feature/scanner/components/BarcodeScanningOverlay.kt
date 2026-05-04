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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeScanningOverlay(
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.6f),
    cornerColor: Color = Color(0xFF00E5FF),
    laserColor: Color = Color.Red,
    frameWidthRatio: Float = 0.8f,
    frameHeightRatio: Float = 0.45f,
    cornerSize: Dp = 18.dp,
    strokeWidth: Dp = 4.dp,
    roundedCornerRadius: Dp = 16.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ScanningTransition")

    // Hiệu ứng di chuyển của tia Laser
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserPosition"
    )

    // Hiệu ứng nhấp nháy cho tia Laser
    val laserAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserAlpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val frameWidth = width * frameWidthRatio
        val frameHeight = height * frameHeightRatio
        val left = (width - frameWidth) / 2f
        val top = (height - frameHeight) / 2f
        val right = left + frameWidth
        val bottom = top + frameHeight

        val cornerRadiusPx = roundedCornerRadius.toPx()
        val strokeWidthPx = strokeWidth.toPx()
        val cornerSizePx = cornerSize.toPx()

        // 1. Vẽ Mask (Phần tối xung quanh) với lỗ khoét bo góc ở giữa
        val rect = Rect(left, top, right, bottom)
        val roundRect = RoundRect(rect, CornerRadius(cornerRadiusPx))

        val maskPath = Path().apply {
            addRect(Rect(0f, 0f, width, height))
            addRoundRect(roundRect)
            fillType = PathFillType.EvenOdd
        }
        drawPath(path = maskPath, color = overlayColor)

        // 2. Vẽ 4 góc khung quét (Corners)
        // Top Left
        drawPath(
            path = Path().apply {
                moveTo(left, top + cornerSizePx)
                lineTo(left, top + cornerRadiusPx)
                arcTo(
                    rect = Rect(left, top, left + cornerRadiusPx * 2, top + cornerRadiusPx * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(left + cornerSizePx, top)
            },
            color = cornerColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Top Right
        drawPath(
            path = Path().apply {
                moveTo(right - cornerSizePx, top)
                lineTo(right - cornerRadiusPx, top)
                arcTo(
                    rect = Rect(right - cornerRadiusPx * 2, top, right, top + cornerRadiusPx * 2),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(right, top + cornerSizePx)
            },
            color = cornerColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Bottom Left
        drawPath(
            path = Path().apply {
                moveTo(left, bottom - cornerSizePx)
                lineTo(left, bottom - cornerRadiusPx)
                arcTo(
                    rect = Rect(left, bottom - cornerRadiusPx * 2, left + cornerRadiusPx * 2, bottom),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = -90f,
                    forceMoveTo = false
                )
                lineTo(left + cornerSizePx, bottom)
            },
            color = cornerColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Bottom Right
        drawPath(
            path = Path().apply {
                moveTo(right - cornerSizePx, bottom)
                lineTo(right - cornerRadiusPx, bottom)
                arcTo(
                    rect = Rect(right - cornerRadiusPx * 2, bottom - cornerRadiusPx * 2, right, bottom),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -90f,
                    forceMoveTo = false
                )
                lineTo(right, bottom - cornerSizePx)
            },
            color = cornerColor,
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // 3. Vẽ tia Laser
        val laserY = top + (frameHeight * laserPosition)
        
        // Hiệu ứng quầng sáng (glow) xung quanh tia laser
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    laserColor.copy(alpha = 0f),
                    laserColor.copy(alpha = 0.2f * laserAlpha),
                    laserColor.copy(alpha = 0f)
                ),
                startY = laserY - 12.dp.toPx(),
                endY = laserY + 12.dp.toPx()
            ),
            topLeft = Offset(left, laserY - 12.dp.toPx()),
            size = Size(frameWidth, 24.dp.toPx())
        )

        // Tia laser chính
        val laserBrush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                laserColor.copy(alpha = laserAlpha),
                laserColor,
                laserColor.copy(alpha = laserAlpha),
                Color.Transparent
            ),
            startX = left,
            endX = right
        )

        drawLine(
            brush = laserBrush,
            start = Offset(left + 4.dp.toPx(), laserY),
            end = Offset(right - 4.dp.toPx(), laserY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
