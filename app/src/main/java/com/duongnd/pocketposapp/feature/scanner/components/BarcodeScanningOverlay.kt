package com.duongnd.pocketposapp.feature.scanner.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BarcodeScanningOverlay(
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.7f),
    cornerColor: Color = Color(0xFF00FF88), // Neon Green
    laserColor: Color = Color(0xFF00FF88),
    frameWidthRatio: Float = 0.75f,
    frameHeightRatio: Float = 0.35f,
    cornerSize: Dp = 30.dp,
    strokeWidth: Dp = 3.dp,
    roundedCornerRadius: Dp = 24.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ScanningTransition")

    // Laser scanning animation
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserPosition"
    )

    // Pulse animation for corners
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
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

        // 1. Mask with cut-out
        val rect = Rect(left, top, right, bottom)
        val roundRect = RoundRect(rect, CornerRadius(cornerRadiusPx))

        val maskPath = Path().apply {
            addRect(Rect(0f, 0f, width, height))
            addRoundRect(roundRect)
            fillType = PathFillType.EvenOdd
        }
        drawPath(path = maskPath, color = overlayColor)

        // 2. Neon Corners
        fun drawCorner(l: Float, t: Float, r: Float, b: Float, rotation: Float) {
            rotate(rotation, pivot = Offset((l + r) / 2, (t + b) / 2)) {
                val path = Path().apply {
                    moveTo(l, t + cornerSizePx)
                    lineTo(l, t + cornerRadiusPx)
                    arcTo(
                        rect = Rect(l, t, l + cornerRadiusPx * 2, t + cornerRadiusPx * 2),
                        startAngleDegrees = 180f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    lineTo(l + cornerSizePx, t)
                }
                
                // Glow effect
                drawPath(
                    path = path,
                    color = cornerColor.copy(alpha = 0.2f * pulseAlpha),
                    style = Stroke(width = strokeWidthPx * 4f, cap = StrokeCap.Round)
                )
                // Main line
                drawPath(
                    path = path,
                    color = cornerColor.copy(alpha = pulseAlpha),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }

        // Top Left
        drawCorner(left, top, left + cornerSizePx, top + cornerSizePx, 0f)
        // Top Right
        drawCorner(right - cornerSizePx, top, right, top + cornerSizePx, 90f)
        // Bottom Right
        drawCorner(right - cornerSizePx, bottom - cornerSizePx, right, bottom, 180f)
        // Bottom Left
        drawCorner(left, bottom - cornerSizePx, left + cornerSizePx, bottom, 270f)

        // 3. Futuristic Laser
        val laserY = top + (frameHeight * laserPosition)
        
        // Laser glow gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    laserColor.copy(alpha = 0f),
                    laserColor.copy(alpha = 0.3f),
                    laserColor.copy(alpha = 0f)
                ),
                startY = laserY - 20.dp.toPx(),
                endY = laserY + 20.dp.toPx()
            ),
            topLeft = Offset(left + 10.dp.toPx(), laserY - 20.dp.toPx()),
            size = Size(frameWidth - 20.dp.toPx(), 40.dp.toPx())
        )

        // Main laser line with horizontal gradient
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    laserColor,
                    laserColor,
                    Color.Transparent
                ),
                startX = left,
                endX = right
            ),
            start = Offset(left + 8.dp.toPx(), laserY),
            end = Offset(right - 8.dp.toPx(), laserY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Target crosshair (center) - very subtle
        val centerX = width / 2f
        val centerY = height / 2f
        val crossSize = 10.dp.toPx()
        
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(centerX - crossSize, centerY),
            end = Offset(centerX + crossSize, centerY),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.2f),
            start = Offset(centerX, centerY - crossSize),
            end = Offset(centerX, centerY + crossSize),
            strokeWidth = 1.dp.toPx()
        )
    }
}
