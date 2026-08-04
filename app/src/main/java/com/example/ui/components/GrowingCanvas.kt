package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GrowingCanvas(
    elementType: String, // "TREE", "FLOWER", "PALACE", "HUMAN"
    elementSubType: String, // "MAN", "WOMAN", "CHILD", "OAK", "ROSE", "GOLDEN_PALACE"
    progress: Float, // 0.0f to 1.0f
    isCollapsing: Boolean, // true if user stopped/interrupted session
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "canvas_anim")
    val swayPhase by infiniteTransition.animateFloat(
        initialValue = -0.05f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    val sparklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle"
    )

    // Collapse animation progress (0.0 to 1.0 crumble/wither)
    val collapseAnim = remember { Animatable(0f) }
    LaunchedEffect(isCollapsing) {
        if (isCollapsing) {
            collapseAnim.animateTo(1f, animationSpec = tween(1200))
        } else {
            collapseAnim.snapTo(0f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val groundY = height * 0.82f

        // Draw Ground Bed
        drawGround(width, height, groundY)

        val crumble = collapseAnim.value

        if (crumble > 0f) {
            // Crumble / Wither Animation
            drawCrumblingObject(
                type = elementType,
                subType = elementSubType,
                centerX = centerX,
                groundY = groundY,
                crumble = crumble
            )
        } else {
            // Normal Growing Animation
            when (elementType) {
                "TREE" -> drawGrowingTree(centerX, groundY, progress, swayPhase, sparklePhase)
                "FLOWER" -> drawGrowingFlower(centerX, groundY, progress, swayPhase, sparklePhase)
                "PALACE" -> drawGrowingPalace(centerX, groundY, progress, sparklePhase)
                "HUMAN" -> drawGrowingHuman(centerX, groundY, progress, elementSubType, swayPhase)
                else -> drawGrowingTree(centerX, groundY, progress, swayPhase, sparklePhase)
            }
        }
    }
}

private fun DrawScope.drawGround(width: Float, height: Float, groundY: Float) {
    // Soil / Grass mound
    val path = Path().apply {
        moveTo(0f, groundY + 20f)
        quadraticTo(width / 2f, groundY - 30f, width, groundY + 20f)
        lineTo(width, height)
        lineTo(0f, height)
        close()
    }
    drawPath(
        path = path,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF388E3C), // Emerald Green grass top
                Color(0xFF2E7D32),
                Color(0xFF4E342E)  // Rich brown soil bottom
            ),
            startY = groundY - 30f,
            endY = height
        )
    )

    // Grass blades
    for (i in 0..12) {
        val gx = (width / 14f) * i + 20f
        drawLine(
            color = Color(0xFF66BB6A),
            start = Offset(gx, groundY),
            end = Offset(gx + 6f, groundY - 18f),
            strokeWidth = 4f
        )
    }
}

private fun DrawScope.drawGrowingTree(
    centerX: Float,
    groundY: Float,
    progress: Float,
    sway: Float,
    sparkle: Float
) {
    if (progress <= 0f) return

    val maxTrunkHeight = 220f * progress
    val trunkTopY = groundY - maxTrunkHeight
    val trunkWidth = (30f * progress).coerceAtLeast(6f)

    // Roots
    if (progress > 0.1f) {
        drawLine(
            color = Color(0xFF5D4037),
            start = Offset(centerX, groundY),
            end = Offset(centerX - 40f * progress, groundY + 20f),
            strokeWidth = 8f * progress
        )
        drawLine(
            color = Color(0xFF5D4037),
            start = Offset(centerX, groundY),
            end = Offset(centerX + 40f * progress, groundY + 20f),
            strokeWidth = 8f * progress
        )
    }

    // Trunk with sway
    val swayX = centerX + (maxTrunkHeight * sway)
    val trunkPath = Path().apply {
        moveTo(centerX - trunkWidth / 2f, groundY)
        quadraticTo(
            centerX + swayX * 0.1f, groundY - maxTrunkHeight / 2f,
            swayX - trunkWidth / 4f, trunkTopY
        )
        lineTo(swayX + trunkWidth / 4f, trunkTopY)
        quadraticTo(
            centerX + swayX * 0.1f + trunkWidth / 2f, groundY - maxTrunkHeight / 2f,
            centerX + trunkWidth / 2f, groundY
        )
        close()
    }
    drawPath(trunkPath, Brush.linearGradient(listOf(Color(0xFF6D4C41), Color(0xFF3E2723))))

    // Branches & Foliage Canopy
    if (progress > 0.3f) {
        val canopyProgress = ((progress - 0.3f) / 0.7f).coerceIn(0f, 1f)
        val radius = 100f * canopyProgress

        // Layered Canopy Circles
        drawCircle(
            color = Color(0xFF1B5E20),
            radius = radius * 1.1f,
            center = Offset(swayX - radius * 0.3f, trunkTopY - radius * 0.2f)
        )
        drawCircle(
            color = Color(0xFF2E7D32),
            radius = radius,
            center = Offset(swayX + radius * 0.3f, trunkTopY - radius * 0.3f)
        )
        drawCircle(
            color = Color(0xFF4CAF50),
            radius = radius * 0.85f,
            center = Offset(swayX, trunkTopY - radius * 0.6f)
        )

        // Blooming Golden Fruits
        if (progress > 0.7f) {
            val fruitProgress = ((progress - 0.7f) / 0.3f).coerceIn(0f, 1f)
            val fruitRadius = 12f * fruitProgress
            drawCircle(Color(0xFFFFD700), fruitRadius, Offset(swayX - radius * 0.4f, trunkTopY - radius * 0.4f))
            drawCircle(Color(0xFFFF9800), fruitRadius, Offset(swayX + radius * 0.3f, trunkTopY - radius * 0.5f))
            drawCircle(Color(0xFFFFD700), fruitRadius, Offset(swayX, trunkTopY - radius * 0.8f))
        }

        // Sparkling Pollen Particles
        if (progress >= 0.9f) {
            for (i in 0..6) {
                val pX = swayX + cos((i * 60 + sparkle * 360) * Math.PI / 180).toFloat() * radius * 1.2f
                val pY = (trunkTopY - radius * 0.5f) + sin((i * 60 + sparkle * 360) * Math.PI / 180).toFloat() * radius * 1.2f
                drawCircle(Color(0xFFFFF59D), 4f + sparkle * 3f, Offset(pX, pY))
            }
        }
    }
}

private fun DrawScope.drawGrowingFlower(
    centerX: Float,
    groundY: Float,
    progress: Float,
    sway: Float,
    sparkle: Float
) {
    if (progress <= 0f) return

    val stemHeight = 180f * progress
    val stemTopY = groundY - stemHeight
    val swayX = centerX + (stemHeight * sway)

    // Curved Stem
    val stemPath = Path().apply {
        moveTo(centerX, groundY)
        quadraticTo(centerX + swayX * 0.05f, groundY - stemHeight / 2f, swayX, stemTopY)
    }
    drawPath(stemPath, Color(0xFF4CAF50), style = Stroke(width = 8f * progress.coerceAtLeast(0.3f)))

    // Leaves on Stem
    if (progress > 0.2f) {
        val leafSize = 35f * progress
        drawOval(
            color = Color(0xFF388E3C),
            topLeft = Offset(swayX - leafSize, groundY - stemHeight * 0.4f),
            size = Size(leafSize, leafSize * 0.5f)
        )
        drawOval(
            color = Color(0xFF388E3C),
            topLeft = Offset(swayX + 5f, groundY - stemHeight * 0.6f),
            size = Size(leafSize, leafSize * 0.5f)
        )
    }

    // Blooming Rose Petals
    if (progress > 0.4f) {
        val bloomProgress = ((progress - 0.4f) / 0.6f).coerceIn(0f, 1f)
        val petalRadius = 45f * bloomProgress

        rotate(sway * 100f, pivot = Offset(swayX, stemTopY)) {
            // 8 Layered Petals
            for (i in 0..7) {
                val angle = i * 45f
                rotate(angle, pivot = Offset(swayX, stemTopY)) {
                    drawOval(
                        color = if (i % 2 == 0) Color(0xFFE91E63) else Color(0xFFF48FB1),
                        topLeft = Offset(swayX - petalRadius * 0.4f, stemTopY - petalRadius * 1.1f),
                        size = Size(petalRadius * 0.8f, petalRadius * 1.1f)
                    )
                }
            }
            // Golden Center
            drawCircle(Color(0xFFFFEB3B), petalRadius * 0.4f, Offset(swayX, stemTopY))
        }

        // Butterflies / Sparkles floating
        if (progress >= 0.8f) {
            val bx = swayX + sin(sparkle * Math.PI.toFloat() * 2f) * 60f
            val by = stemTopY - 40f + cos(sparkle * Math.PI.toFloat() * 2f) * 20f
            drawCircle(Color(0xFFFF80AB), 6f, Offset(bx, by))
            drawCircle(Color(0xFFFF4081), 6f, Offset(bx + 12f, by))
        }
    }
}

private fun DrawScope.drawGrowingPalace(
    centerX: Float,
    groundY: Float,
    progress: Float,
    sparkle: Float
) {
    if (progress <= 0f) return

    val palaceWidth = 240f * progress
    val palaceHeight = 180f * progress
    val palaceLeft = centerX - palaceWidth / 2f
    val palaceTop = groundY - palaceHeight

    // 1. Foundation Base
    drawRoundRect(
        color = Color(0xFF78909C),
        topLeft = Offset(palaceLeft, groundY - 24f * progress),
        size = Size(palaceWidth, 24f * progress),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // 2. Main Palace Body
    if (progress > 0.2f) {
        val bodyProgress = ((progress - 0.2f) / 0.8f).coerceIn(0f, 1f)
        drawRect(
            brush = Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC))),
            topLeft = Offset(palaceLeft + 20f, groundY - (120f * bodyProgress)),
            size = Size(palaceWidth - 40f, 100f * bodyProgress)
        )

        // Pillars
        val pillarWidth = 14f
        for (i in 0..4) {
            val px = palaceLeft + 35f + i * ((palaceWidth - 90f) / 4f)
            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(px, groundY - (120f * bodyProgress)),
                size = Size(pillarWidth, 96f * bodyProgress)
            )
        }

        // Grand Door
        drawRoundRect(
            color = Color(0xFF4E342E),
            topLeft = Offset(centerX - 20f, groundY - 50f * bodyProgress),
            size = Size(40f, 50f * bodyProgress),
            cornerRadius = CornerRadius(20f, 0f)
        )
    }

    // 3. Golden Roof & Spire Domes
    if (progress > 0.6f) {
        val roofProgress = ((progress - 0.6f) / 0.4f).coerceIn(0f, 1f)

        // Central Golden Dome
        val domeRadius = 40f * roofProgress
        val domeCenterY = palaceTop + 30f
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8F00))),
            radius = domeRadius,
            center = Offset(centerX, domeCenterY)
        )

        // Golden Spire & Flag
        drawLine(
            color = Color(0xFFFFD700),
            start = Offset(centerX, domeCenterY - domeRadius),
            end = Offset(centerX, domeCenterY - domeRadius - 30f * roofProgress),
            strokeWidth = 6f
        )

        // Waving Flag
        val flagPath = Path().apply {
            moveTo(centerX, domeCenterY - domeRadius - 30f * roofProgress)
            lineTo(centerX + 25f, domeCenterY - domeRadius - 20f * roofProgress)
            lineTo(centerX, domeCenterY - domeRadius - 10f * roofProgress)
            close()
        }
        drawPath(flagPath, Color(0xFFD32F2F))

        // Side Domes
        drawCircle(
            color = Color(0xFFFFB300),
            radius = domeRadius * 0.6f,
            center = Offset(palaceLeft + 30f, groundY - 110f)
        )
        drawCircle(
            color = Color(0xFFFFB300),
            radius = domeRadius * 0.6f,
            center = Offset(palaceLeft + palaceWidth - 30f, groundY - 110f)
        )
    }

    // Fireworks / Golden Sparkles
    if (progress >= 0.95f) {
        for (i in 0..7) {
            val angle = i * 45f + sparkle * 360f
            val rad = (angle * Math.PI / 180).toFloat()
            val sx = centerX + cos(rad) * (80f + sparkle * 30f)
            val sy = (palaceTop - 40f) + sin(rad) * (80f + sparkle * 30f)
            drawCircle(Color(0xFFFFE082), 5f + sparkle * 4f, Offset(sx, sy))
        }
    }
}

private fun DrawScope.drawGrowingHuman(
    centerX: Float,
    groundY: Float,
    progress: Float,
    subType: String, // "MAN", "WOMAN", "CHILD"
    sway: Float
) {
    if (progress <= 0f) return

    val scale = when (subType) {
        "CHILD" -> 0.65f * progress
        "WOMAN" -> 0.9f * progress
        else -> 1.0f * progress
    }

    val headRadius = 24f * scale
    val bodyHeight = 70f * scale
    val humanTopY = groundY - (bodyHeight + headRadius * 2.5f)
    val headCenterY = humanTopY + headRadius

    // Waving Arm Animation
    val armAngle = sway * 400f

    // 1. Legs
    drawLine(
        color = Color(0xFF1565C0),
        start = Offset(centerX - 12f * scale, groundY - bodyHeight * 0.4f),
        end = Offset(centerX - 16f * scale, groundY),
        strokeWidth = 10f * scale
    )
    drawLine(
        color = Color(0xFF1565C0),
        start = Offset(centerX + 12f * scale, groundY - bodyHeight * 0.4f),
        end = Offset(centerX + 16f * scale, groundY),
        strokeWidth = 10f * scale
    )

    // 2. Torso (Body Shirt)
    val shirtColor = when (subType) {
        "WOMAN" -> Color(0xFFE91E63) // Pink dress
        "CHILD" -> Color(0xFFFF9800) // Orange shirt
        else -> Color(0xFF2E7D32)    // Green jacket
    }
    drawRoundRect(
        color = shirtColor,
        topLeft = Offset(centerX - 20f * scale, headCenterY + headRadius),
        size = Size(40f * scale, bodyHeight * 0.6f),
        cornerRadius = CornerRadius(12f * scale)
    )

    // 3. Waving Arms
    val shoulderY = headCenterY + headRadius + 10f * scale
    // Left arm resting
    drawLine(
        color = shirtColor,
        start = Offset(centerX - 20f * scale, shoulderY),
        end = Offset(centerX - 32f * scale, shoulderY + 35f * scale),
        strokeWidth = 8f * scale
    )
    // Right arm waving!
    rotate(armAngle, pivot = Offset(centerX + 20f * scale, shoulderY)) {
        drawLine(
            color = shirtColor,
            start = Offset(centerX + 20f * scale, shoulderY),
            end = Offset(centerX + 35f * scale, shoulderY - 25f * scale),
            strokeWidth = 8f * scale
        )
        // Hand
        drawCircle(Color(0xFFFFCC80), 6f * scale, Offset(centerX + 35f * scale, shoulderY - 25f * scale))
    }

    // 4. Head & Face
    drawCircle(Color(0xFFFFCC80), headRadius, Offset(centerX, headCenterY))

    // Hair
    when (subType) {
        "WOMAN" -> {
            // Long hair
            drawArc(
                color = Color(0xFF3E2723),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius - 4f, headCenterY - headRadius - 4f),
                size = Size((headRadius + 4f) * 2f, (headRadius + 8f) * 2f)
            )
        }
        "CHILD" -> {
            // Cute cap
            drawArc(
                color = Color(0xFF00BCD4),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius, headCenterY - headRadius - 2f),
                size = Size(headRadius * 2f, headRadius * 1.5f)
            )
        }
        else -> {
            // Short hair
            drawArc(
                color = Color(0xFF212121),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - headRadius, headCenterY - headRadius),
                size = Size(headRadius * 2f, headRadius * 1.2f)
            )
        }
    }

    // Smiling Face
    drawCircle(Color(0xFF212121), 3f * scale, Offset(centerX - 7f * scale, headCenterY - 2f))
    drawCircle(Color(0xFF212121), 3f * scale, Offset(centerX + 7f * scale, headCenterY - 2f))
    drawArc(
        color = Color(0xFFD32F2F),
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = Offset(centerX - 8f * scale, headCenterY + 2f),
        size = Size(16f * scale, 12f * scale),
        style = Stroke(width = 3f * scale)
    )
}

private fun DrawScope.drawCrumblingObject(
    type: String,
    subType: String,
    centerX: Float,
    groundY: Float,
    crumble: Float // 0.0 to 1.0
) {
    val dustY = groundY - (50f * (1f - crumble))

    // Shaking offset
    val shakeX = if (crumble < 0.8f) (sin(crumble * 50f) * 15f) else 0f

    // Falling Ash / Dust Debris Particles
    for (i in 0..15) {
        val px = centerX + shakeX + (cos(i * 24f) * (i * 8f))
        val py = dustY + (crumble * 80f) + (sin(i * 30f) * 20f)
        val particleColor = when (type) {
            "TREE" -> Color(0xFF5D4037) // Brown wood ash
            "FLOWER" -> Color(0xFF8D6E63) // Dried leaf dust
            "PALACE" -> Color(0xFF90A4AE) // Stone crumble
            else -> Color(0xFF757575)
        }
        drawCircle(particleColor, (8f * (1f - crumble)).coerceAtLeast(1f), Offset(px, py))
    }

    // Shattered Ground Crack
    drawLine(
        color = Color(0xFF212121),
        start = Offset(centerX - 80f + shakeX, groundY),
        end = Offset(centerX + 80f + shakeX, groundY + 10f),
        strokeWidth = 6f * crumble
    )
}
