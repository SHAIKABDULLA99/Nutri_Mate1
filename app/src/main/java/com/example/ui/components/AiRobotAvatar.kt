package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * 100% faithful vector implementation of the user's AI Bot mascot image:
 * - Round white robot head with cyan blue top cap and blue ear cylinders
 * - Black curved visor with cyan blue glowing eyes
 * - White chest armor with black monitor displaying glowing cyan "AI"
 * - Articulated black arms with white forearm cuffs and claw hands
 * - Compact legs and ground shadow
 */
@Composable
fun AiRobotAvatar(
    modifier: Modifier = Modifier.size(72.dp),
    isAnimated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_anim")
    val floatOffset by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "hover"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val eyeGlowPulse by if (isAnimated) {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "eyeGlow"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .testTag("ai_robot_avatar_image"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val scale = (w.coerceAtMost(h)) / 100f

            val strokeDark = Color(0xFF1E293B) // Dark outline
            val strokeThick = 2.4f * scale
            val strokeMedium = 1.6f * scale

            val bodyWhite = Color(0xFFFFFFFF)
            val bodyShadowWhite = Color(0xFFE2E8F0)
            val screenBlack = Color(0xFF0F172A)
            val cyanLight = Color(0xFF7DD3FC)
            val cyanBright = Color(0xFF38BDF8)
            val cyanDeep = Color(0xFF0284C7)
            val shadowColor = Color(0xFFCBD5E1).copy(alpha = 0.75f)

            val yShift = floatOffset * scale

            // 1. Ground Shadow
            drawOval(
                color = shadowColor,
                topLeft = Offset(26f * scale, (89f * scale) + (yShift * 0.2f)),
                size = Size(48f * scale, 6f * scale)
            )

            // 2. Legs / Feet
            // Left Leg
            val leftLegPath = Path().apply {
                moveTo(38f * scale, (70f * scale) + yShift)
                lineTo(38f * scale, (80f * scale) + yShift)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        38f * scale,
                        (75f * scale) + yShift,
                        46f * scale,
                        (83f * scale) + yShift
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(46f * scale, (70f * scale) + yShift)
                close()
            }
            drawPath(leftLegPath, color = strokeDark, style = Fill)
            drawPath(
                leftLegPath,
                color = strokeDark,
                style = Stroke(width = strokeMedium, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Left leg knee highlight
            drawRoundRect(
                color = Color(0xFF475569),
                topLeft = Offset(40f * scale, (76f * scale) + yShift),
                size = Size(4f * scale, 4f * scale),
                cornerRadius = CornerRadius(2f * scale, 2f * scale)
            )

            // Right Leg
            val rightLegPath = Path().apply {
                moveTo(54f * scale, (70f * scale) + yShift)
                lineTo(54f * scale, (80f * scale) + yShift)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        54f * scale,
                        (75f * scale) + yShift,
                        62f * scale,
                        (83f * scale) + yShift
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(62f * scale, (70f * scale) + yShift)
                close()
            }
            drawPath(rightLegPath, color = strokeDark, style = Fill)
            drawPath(
                rightLegPath,
                color = strokeDark,
                style = Stroke(width = strokeMedium, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Right leg knee highlight
            drawRoundRect(
                color = Color(0xFF475569),
                topLeft = Offset(56f * scale, (76f * scale) + yShift),
                size = Size(4f * scale, 4f * scale),
                cornerRadius = CornerRadius(2f * scale, 2f * scale)
            )

            // 3. Arms and Hands
            // Left Shoulder joint
            drawCircle(
                color = strokeDark,
                radius = 4f * scale,
                center = Offset(29f * scale, (50f * scale) + yShift)
            )
            drawCircle(
                color = cyanDeep,
                radius = 2.2f * scale,
                center = Offset(29f * scale, (50f * scale) + yShift)
            )

            // Left Arm curve
            val leftArmPath = Path().apply {
                moveTo(28f * scale, (50f * scale) + yShift)
                cubicTo(
                    23f * scale, (54f * scale) + yShift,
                    23f * scale, (65f * scale) + yShift,
                    26f * scale, (74f * scale) + yShift
                )
            }
            drawPath(leftArmPath, color = strokeDark, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))

            // Left Forearm white cuff
            drawRoundRect(
                color = bodyWhite,
                topLeft = Offset(22f * scale, (61f * scale) + yShift),
                size = Size(7f * scale, 12f * scale),
                cornerRadius = CornerRadius(3.5f * scale, 3.5f * scale)
            )
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(22f * scale, (61f * scale) + yShift),
                size = Size(7f * scale, 12f * scale),
                cornerRadius = CornerRadius(3.5f * scale, 3.5f * scale),
                style = Stroke(width = strokeMedium)
            )

            // Left Hand Claw (2 prongs)
            val leftClaw = Path().apply {
                moveTo(23f * scale, (73f * scale) + yShift)
                cubicTo(
                    20f * scale, (76f * scale) + yShift,
                    20f * scale, (80f * scale) + yShift,
                    25f * scale, (80f * scale) + yShift
                )
                moveTo(28f * scale, (73f * scale) + yShift)
                cubicTo(
                    31f * scale, (76f * scale) + yShift,
                    29f * scale, (80f * scale) + yShift,
                    25f * scale, (80f * scale) + yShift
                )
            }
            drawPath(leftClaw, color = strokeDark, style = Stroke(width = 2.4f * scale, cap = StrokeCap.Round))

            // Right Shoulder joint
            drawCircle(
                color = strokeDark,
                radius = 4f * scale,
                center = Offset(71f * scale, (50f * scale) + yShift)
            )
            drawCircle(
                color = cyanDeep,
                radius = 2.2f * scale,
                center = Offset(71f * scale, (50f * scale) + yShift)
            )

            // Right Arm curve
            val rightArmPath = Path().apply {
                moveTo(72f * scale, (50f * scale) + yShift)
                cubicTo(
                    77f * scale, (54f * scale) + yShift,
                    77f * scale, (65f * scale) + yShift,
                    74f * scale, (74f * scale) + yShift
                )
            }
            drawPath(rightArmPath, color = strokeDark, style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round))

            // Right Forearm white cuff
            drawRoundRect(
                color = bodyWhite,
                topLeft = Offset(71f * scale, (61f * scale) + yShift),
                size = Size(7f * scale, 12f * scale),
                cornerRadius = CornerRadius(3.5f * scale, 3.5f * scale)
            )
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(71f * scale, (61f * scale) + yShift),
                size = Size(7f * scale, 12f * scale),
                cornerRadius = CornerRadius(3.5f * scale, 3.5f * scale),
                style = Stroke(width = strokeMedium)
            )

            // Right Hand Claw (2 prongs)
            val rightClaw = Path().apply {
                moveTo(77f * scale, (73f * scale) + yShift)
                cubicTo(
                    80f * scale, (76f * scale) + yShift,
                    80f * scale, (80f * scale) + yShift,
                    75f * scale, (80f * scale) + yShift
                )
                moveTo(72f * scale, (73f * scale) + yShift)
                cubicTo(
                    69f * scale, (76f * scale) + yShift,
                    71f * scale, (80f * scale) + yShift,
                    75f * scale, (80f * scale) + yShift
                )
            }
            drawPath(rightClaw, color = strokeDark, style = Stroke(width = 2.4f * scale, cap = StrokeCap.Round))

            // 4. Torso / Body (Shield shape)
            val torsoPath = Path().apply {
                moveTo(34f * scale, (45f * scale) + yShift)
                lineTo(66f * scale, (45f * scale) + yShift)
                cubicTo(
                    68f * scale, (58f * scale) + yShift,
                    64f * scale, (72f * scale) + yShift,
                    50f * scale, (72f * scale) + yShift
                )
                cubicTo(
                    36f * scale, (72f * scale) + yShift,
                    32f * scale, (58f * scale) + yShift,
                    34f * scale, (45f * scale) + yShift
                )
                close()
            }
            drawPath(torsoPath, color = bodyWhite, style = Fill)
            drawPath(
                torsoPath,
                color = strokeDark,
                style = Stroke(width = strokeThick, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 5. Torso Screen (Black rounded box with glowing "AI")
            val screenLeft = 40f * scale
            val screenTop = (50f * scale) + yShift
            val screenWidth = 20f * scale
            val screenHeight = 15f * scale

            drawRoundRect(
                color = screenBlack,
                topLeft = Offset(screenLeft, screenTop),
                size = Size(screenWidth, screenHeight),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )

            // Draw "AI" Letters glowing on the chest screen
            drawAiTextOnScreen(
                centerX = 50f * scale,
                centerY = (57.5f * scale) + yShift,
                scale = scale,
                color = cyanBright,
                glowColor = cyanLight
            )

            // 6. Neck joint
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(43f * scale, (39f * scale) + yShift),
                size = Size(14f * scale, 7f * scale),
                cornerRadius = CornerRadius(2f * scale, 2f * scale)
            )

            // 7. Ear Cylinders on Head sides
            // Left Ear
            drawRoundRect(
                color = bodyWhite,
                topLeft = Offset(24f * scale, (24f * scale) + yShift),
                size = Size(8f * scale, 12f * scale),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(24f * scale, (24f * scale) + yShift),
                size = Size(8f * scale, 12f * scale),
                cornerRadius = CornerRadius(4f * scale, 4f * scale),
                style = Stroke(width = strokeMedium)
            )
            drawCircle(
                color = cyanDeep,
                radius = 2.4f * scale,
                center = Offset(27f * scale, (30f * scale) + yShift)
            )

            // Right Ear
            drawRoundRect(
                color = bodyWhite,
                topLeft = Offset(68f * scale, (24f * scale) + yShift),
                size = Size(8f * scale, 12f * scale),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(68f * scale, (24f * scale) + yShift),
                size = Size(8f * scale, 12f * scale),
                cornerRadius = CornerRadius(4f * scale, 4f * scale),
                style = Stroke(width = strokeMedium)
            )
            drawCircle(
                color = cyanDeep,
                radius = 2.4f * scale,
                center = Offset(73f * scale, (30f * scale) + yShift)
            )

            // 8. Head Shell
            val headLeft = 28f * scale
            val headTop = (16f * scale) + yShift
            val headWidth = 44f * scale
            val headHeight = 27f * scale
            val headRadius = 13.5f * scale

            drawRoundRect(
                color = bodyWhite,
                topLeft = Offset(headLeft, headTop),
                size = Size(headWidth, headHeight),
                cornerRadius = CornerRadius(headRadius, headRadius)
            )
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(headLeft, headTop),
                size = Size(headWidth, headHeight),
                cornerRadius = CornerRadius(headRadius, headRadius),
                style = Stroke(width = strokeThick)
            )

            // 9. Top Head Cyan Accent Cap
            val capPath = Path().apply {
                moveTo(41f * scale, (17f * scale) + yShift)
                cubicTo(
                    45f * scale, (14.5f * scale) + yShift,
                    55f * scale, (14.5f * scale) + yShift,
                    59f * scale, (17f * scale) + yShift
                )
                lineTo(57f * scale, (19f * scale) + yShift)
                cubicTo(
                    54f * scale, (17.5f * scale) + yShift,
                    46f * scale, (17.5f * scale) + yShift,
                    43f * scale, (19f * scale) + yShift
                )
                close()
            }
            drawPath(capPath, color = cyanLight, style = Fill)
            drawPath(capPath, color = strokeDark, style = Stroke(width = 1.2f * scale))

            // 10. Dark Face Visor Screen
            val visorLeft = 32.5f * scale
            val visorTop = (22f * scale) + yShift
            val visorWidth = 35f * scale
            val visorHeight = 16.5f * scale
            val visorRadius = 8f * scale

            drawRoundRect(
                color = screenBlack,
                topLeft = Offset(visorLeft, visorTop),
                size = Size(visorWidth, visorHeight),
                cornerRadius = CornerRadius(visorRadius, visorRadius)
            )
            drawRoundRect(
                color = strokeDark,
                topLeft = Offset(visorLeft, visorTop),
                size = Size(visorWidth, visorHeight),
                cornerRadius = CornerRadius(visorRadius, visorRadius),
                style = Stroke(width = strokeMedium)
            )

            // 11. Cyan Glowing Eyes
            val eyeRadius = 4.2f * scale * eyeGlowPulse
            val leftEyeCenter = Offset(42f * scale, (30.2f * scale) + yShift)
            val rightEyeCenter = Offset(58f * scale, (30.2f * scale) + yShift)

            // Left Eye
            drawCircle(color = cyanDeep, radius = 4.8f * scale, center = leftEyeCenter)
            drawCircle(color = cyanBright, radius = eyeRadius, center = leftEyeCenter)
            drawCircle(color = Color.White, radius = 1.4f * scale, center = leftEyeCenter + Offset(-1.2f * scale, -1.2f * scale))
            drawCircle(color = strokeDark, radius = 2.2f * scale, center = leftEyeCenter, style = Stroke(width = 1.1f * scale))

            // Right Eye
            drawCircle(color = cyanDeep, radius = 4.8f * scale, center = rightEyeCenter)
            drawCircle(color = cyanBright, radius = eyeRadius, center = rightEyeCenter)
            drawCircle(color = Color.White, radius = 1.4f * scale, center = rightEyeCenter + Offset(-1.2f * scale, -1.2f * scale))
            drawCircle(color = strokeDark, radius = 2.2f * scale, center = rightEyeCenter, style = Stroke(width = 1.1f * scale))
        }
    }
}

/**
 * Helper to crisply draw the letters "AI" on the chest screen
 */
private fun DrawScope.drawAiTextOnScreen(
    centerX: Float,
    centerY: Float,
    scale: Float,
    color: Color,
    glowColor: Color
) {
    val strokeW = 1.8f * scale

    // Letter 'A'
    val aPath = Path().apply {
        moveTo(centerX - (6.5f * scale), centerY + (4.5f * scale))
        lineTo(centerX - (3.5f * scale), centerY - (4.5f * scale))
        lineTo(centerX - (0.5f * scale), centerY + (4.5f * scale))
        moveTo(centerX - (5.2f * scale), centerY + (1.2f * scale))
        lineTo(centerX - (1.8f * scale), centerY + (1.2f * scale))
    }
    drawPath(aPath, color = color, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Letter 'I'
    val iPath = Path().apply {
        moveTo(centerX + (2.5f * scale), centerY - (4.5f * scale))
        lineTo(centerX + (6.5f * scale), centerY - (4.5f * scale))
        moveTo(centerX + (4.5f * scale), centerY - (4.5f * scale))
        lineTo(centerX + (4.5f * scale), centerY + (4.5f * scale))
        moveTo(centerX + (2.5f * scale), centerY + (4.5f * scale))
        lineTo(centerX + (6.5f * scale), centerY + (4.5f * scale))
    }
    drawPath(iPath, color = color, style = Stroke(width = strokeW, cap = StrokeCap.Round))
}
