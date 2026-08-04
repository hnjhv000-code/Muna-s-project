package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.data.Achievement
import com.example.util.LanguageManager
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LandCanvas(
    achievements: List<Achievement>,
    isDarkTheme: Boolean,
    onItemTapped: (Achievement) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    var tapMessage by remember { mutableStateOf("") }

    // Pinch-to-zoom and Pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "land_anim")
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clouds"
    )

    val bouncePhase by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.6f, 4f)
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
            .pointerInput(achievements, scale, offsetX, offsetY) {
                detectTapGestures { tapOffset ->
                    val width = size.width.toFloat()
                    val height = size.height.toFloat()

                    // Convert tap coordinates to local canvas space
                    val localX = (tapOffset.x - offsetX) / scale
                    val localY = (tapOffset.y - offsetY) / scale

                    // Check which item was tapped
                    for (item in achievements) {
                        val ix = item.posX * (width * 0.85f) + width * 0.075f
                        val iy = item.posY * (height * 0.65f) + height * 0.25f
                        val dist = (localX - ix) * (localX - ix) + (localY - iy) * (localY - iy)
                        if (dist <= 60f * 60f) {
                            selectedAchievement = item
                            onItemTapped(item)
                            tapMessage = when (item.type) {
                                "HUMAN" -> when (LanguageManager.currentLanguage.code) {
                                    "AR" -> "أهلاً بك في أرضنا العائلية السعيدة! 👨‍👩‍👧"
                                    "FR" -> "Bienvenue dans notre terre familiale! 👨‍👩‍👧"
                                    "JA" -> "私たちの幸せな土地へようこそ！ 👨‍👩‍👧"
                                    "ZH" -> "欢迎来到幸福家园！ 👨‍👩‍👧"
                                    else -> "Welcome to our happy family land! 👨‍👩‍👧"
                                }
                                "PALACE" -> "🏰 ${item.title}"
                                "TREE" -> "🌳 ${item.title}"
                                "FLOWER" -> "🌸 ${item.title}"
                                else -> "✨ ${item.title}"
                            }
                            break
                        }
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height

        withTransform({
            translate(offsetX, offsetY)
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            // 1. Draw Sky & Terrain
            drawLandBackground(width, height, isDarkTheme, cloudOffset)

            // 2. Draw Family Connection Paths
            drawFamilyConnections(achievements, width, height, isDarkTheme)

            // 3. Draw All Completed Achievements
            for (item in achievements) {
                val ix = item.posX * (width * 0.85f) + width * 0.075f
                val iy = item.posY * (height * 0.65f) + height * 0.25f

                val isSelected = selectedAchievement?.id == item.id
                val yOffset = if (isSelected) bouncePhase else 0f

                drawLandItem(
                    item = item,
                    x = ix,
                    y = iy + yOffset,
                    isDark = isDarkTheme,
                    isSelected = isSelected
                )
            }

            // 4. Draw Interactive Speech Bubble if Tapped
            selectedAchievement?.let { item ->
                val ix = item.posX * (width * 0.85f) + width * 0.075f
                val iy = item.posY * (height * 0.65f) + height * 0.25f + bouncePhase - 60f

                val layoutResult = textMeasurer.measure(
                    text = tapMessage,
                    style = TextStyle(color = Color.White, fontSize = 13.sp)
                )

                val bubbleWidth = layoutResult.size.width + 30f
                val bubbleHeight = layoutResult.size.height + 16f
                val bubbleLeft = (ix - bubbleWidth / 2f).coerceIn(10f, width - bubbleWidth - 10f)

                drawRoundRect(
                    color = if (isDarkTheme) Color(0xFF00E676) else Color(0xFF2E7D32),
                    topLeft = Offset(bubbleLeft, iy - bubbleHeight),
                    size = Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(12f)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = tapMessage,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 13.sp
                    ),
                    topLeft = Offset(bubbleLeft + 15f, iy - bubbleHeight + 8f)
                )
            }
        }
    }
}

private fun DrawScope.drawLandBackground(
    width: Float,
    height: Float,
    isDark: Boolean,
    cloudOffset: Float
) {
    // Sky
    val skyBrush = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF030A16), Color(0xFF0A192F)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF81D4FA), Color(0xFFE1F5FE)))
    }
    drawRect(skyBrush, topLeft = Offset.Zero, size = Size(width, height * 0.35f))

    // Clouds or Stars
    if (isDark) {
        // Neon Stars
        for (i in 0..15) {
            val sx = (i * 70f + 30f) % width
            val sy = (i * 15f + 20f) % (height * 0.25f)
            drawCircle(Color(0xFF80D8FF), 2.5f, Offset(sx, sy))
        }
    } else {
        // Soft White Clouds
        val cx1 = (cloudOffset * width * 1.2f) % (width + 100f) - 50f
        drawCircle(Color.White.copy(alpha = 0.8f), 35f, Offset(cx1, 50f))
        drawCircle(Color.White.copy(alpha = 0.8f), 25f, Offset(cx1 + 30f, 40f))

        val cx2 = ((cloudOffset + 0.5f) * width * 1.2f) % (width + 100f) - 50f
        drawCircle(Color.White.copy(alpha = 0.7f), 40f, Offset(cx2, 90f))
    }

    // Rolling Hills Grassland
    val terrainPath = Path().apply {
        moveTo(0f, height * 0.3f)
        quadraticTo(width * 0.3f, height * 0.25f, width * 0.6f, height * 0.32f)
        quadraticTo(width * 0.85f, height * 0.38f, width, height * 0.3f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }

    val grassBrush = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF003822), Color(0xFF001F12)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)))
    }
    drawPath(terrainPath, grassBrush)

    // Agricultural Winding Paths
    val path = Path().apply {
        moveTo(width * 0.1f, height)
        quadraticTo(width * 0.4f, height * 0.65f, width * 0.5f, height * 0.32f)
    }
    drawPath(
        path = path,
        color = if (isDark) Color(0xFF00E676).copy(alpha = 0.3f) else Color(0xFF8D6E63),
        style = Stroke(width = 16f)
    )
}

private fun DrawScope.drawFamilyConnections(
    items: List<Achievement>,
    width: Float,
    height: Float,
    isDark: Boolean
) {
    val mapById = items.associateBy { it.id }
    for (item in items) {
        if (item.type == "HUMAN" && item.parentManId != null && item.parentWomanId != null) {
            val man = mapById[item.parentManId]
            val woman = mapById[item.parentWomanId]
            if (man != null && woman != null) {
                val cx = item.posX * (width * 0.85f) + width * 0.075f
                val cy = item.posY * (height * 0.65f) + height * 0.25f

                val mx = man.posX * (width * 0.85f) + width * 0.075f
                val my = man.posY * (height * 0.65f) + height * 0.25f

                val wx = woman.posX * (width * 0.85f) + width * 0.075f
                val wy = woman.posY * (height * 0.65f) + height * 0.25f

                // Golden Heart Line connecting Parents to Child
                val path = Path().apply {
                    moveTo(mx, my)
                    lineTo(cx, cy)
                    lineTo(wx, wy)
                }
                drawPath(
                    path = path,
                    color = if (isDark) Color(0xFFFFD700) else Color(0xFFFF4081),
                    style = Stroke(width = 4f)
                )

                // Heart Symbol at child
                drawCircle(Color(0xFFFF4081), 8f, Offset(cx, cy - 35f))
            }
        }
    }
}

private fun DrawScope.drawLandItem(
    item: Achievement,
    x: Float,
    y: Float,
    isDark: Boolean,
    isSelected: Boolean
) {
    // Selection Halo
    if (isSelected) {
        drawCircle(
            color = if (isDark) Color(0xFF00E676) else Color(0xFFFFD700),
            radius = 38f,
            center = Offset(x, y - 10f),
            style = Stroke(width = 4f)
        )
    }

    when (item.type) {
        "TREE" -> {
            // Trunk
            drawRect(Color(0xFF5D4037), topLeft = Offset(x - 5f, y - 15f), size = Size(10f, 25f))
            // Foliage
            drawCircle(if (isDark) Color(0xFF00E676) else Color(0xFF388E3C), 26f, Offset(x, y - 30f))
            drawCircle(if (isDark) Color(0xFFB9F6CA) else Color(0xFF66BB6A), 18f, Offset(x - 8f, y - 36f))
        }
        "FLOWER" -> {
            // Stem
            drawLine(Color(0xFF4CAF50), start = Offset(x, y + 10f), end = Offset(x, y - 15f), strokeWidth = 4f)
            // Petals
            for (i in 0..5) {
                rotate(i * 60f, pivot = Offset(x, y - 15f)) {
                    drawCircle(Color(0xFFF48FB1), 10f, Offset(x, y - 24f))
                }
            }
            drawCircle(Color(0xFFFFEB3B), 7f, Offset(x, y - 15f))
        }
        "PALACE" -> {
            // Base
            drawRoundRect(
                color = if (isDark) Color(0xFFFFD700) else Color(0xFFB0BEC5),
                topLeft = Offset(x - 22f, y - 12f),
                size = Size(44f, 24f),
                cornerRadius = CornerRadius(4f)
            )
            // Golden Dome
            drawCircle(Color(0xFFFFB300), 16f, Offset(x, y - 20f))
            drawLine(Color(0xFFFFD700), start = Offset(x, y - 36f), end = Offset(x, y - 46f), strokeWidth = 3f)
            // Flag
            val flagPath = Path().apply {
                moveTo(x, y - 46f)
                lineTo(x + 12f, y - 41f)
                lineTo(x, y - 36f)
                close()
            }
            drawPath(flagPath, Color(0xFFD32F2F))
        }
        "HUMAN" -> {
            val isAdult = item.growthStage >= 2
            val hRadius = if (isAdult) 12f else 8f
            val hBodyHeight = if (isAdult) 24f else 16f

            // Head
            drawCircle(Color(0xFFFFCC80), hRadius, Offset(x, y - hBodyHeight - hRadius))
            // Shirt
            val color = when (item.subType) {
                "WOMAN" -> Color(0xFFE91E63)
                "CHILD" -> Color(0xFFFF9800)
                else -> Color(0xFF1976D2)
            }
            drawRoundRect(
                color = color,
                topLeft = Offset(x - hRadius, y - hBodyHeight),
                size = Size(hRadius * 2f, hBodyHeight),
                cornerRadius = CornerRadius(6f)
            )
            // Legs
            drawLine(Color(0xFF212121), start = Offset(x - 4f, y), end = Offset(x - 4f, y + 10f), strokeWidth = 3f)
            drawLine(Color(0xFF212121), start = Offset(x + 4f, y), end = Offset(x + 4f, y + 10f), strokeWidth = 3f)
        }
    }
}
