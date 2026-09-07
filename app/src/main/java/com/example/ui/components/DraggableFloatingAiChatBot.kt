package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Draggable, floating AI Chatbot avatar mascot that appears across all screens.
 * The user can drag and move it anywhere on the screen, and tap to open the AI Chat.
 * Contains purely the robot image avatar (no text / words).
 */
@Composable
fun DraggableFloatingAiChatBot(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val avatarSize = 64.dp
        val avatarSizePx = with(density) { avatarSize.toPx() }
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        // Default initial position at bottom-right
        var offsetX by remember { mutableFloatStateOf(maxWidthPx - avatarSizePx - with(density) { 20.dp.toPx() }) }
        var offsetY by remember { mutableFloatStateOf(maxHeightPx - avatarSizePx - with(density) { 90.dp.toPx() }) }

        // Recalculate bounds if screen dimensions change
        LaunchedEffect(maxWidthPx, maxHeightPx) {
            if (offsetX <= 0f || offsetX > maxWidthPx - avatarSizePx) {
                offsetX = (maxWidthPx - avatarSizePx - with(density) { 20.dp.toPx() }).coerceAtLeast(0f)
            }
            if (offsetY <= 0f || offsetY > maxHeightPx - avatarSizePx) {
                offsetY = (maxHeightPx - avatarSizePx - with(density) { 90.dp.toPx() }).coerceAtLeast(0f)
            }
        }

        var isDragging by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .size(avatarSize)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                        },
                        onDragEnd = {
                            isDragging = false
                            // Keep safely inside viewport bounds
                            offsetX = offsetX.coerceIn(0f, (maxWidthPx - avatarSizePx).coerceAtLeast(0f))
                            offsetY = offsetY.coerceIn(0f, (maxHeightPx - avatarSizePx).coerceAtLeast(0f))
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, (maxWidthPx - avatarSizePx).coerceAtLeast(0f))
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, (maxHeightPx - avatarSizePx).coerceAtLeast(0f))
                        }
                    )
                }
        ) {
            Surface(
                onClick = {
                    if (!isDragging) {
                        onClick()
                    }
                },
                shape = CircleShape,
                color = Color.Transparent,
                shadowElevation = if (isDragging) 12.dp else 6.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("floating_draggable_chatbot_avatar")
            ) {
                AiRobotAvatar(
                    modifier = Modifier.fillMaxSize(),
                    isAnimated = true
                )
            }
        }
    }
}
