package com.test.camerax.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.camerax.ui.theme.CameraXTheme

@Composable
internal fun CameraButton(
    onPhotoTaken: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        val infiniteTransition = rememberInfiniteTransition(label = "")
        var isPressed by remember { mutableStateOf(false) }

        val size by infiniteTransition.animateValue(
            initialValue = if (!isPressed) 0.dp else 180.dp,
            targetValue = if (!isPressed) 0.dp else 140.dp,
            Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Circle(
                size = size,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f)
            )

            Circle(
                size = 130.dp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onPhotoTaken()
                            },
                            onPress = {
                                awaitRelease()
                                isPressed = false
                                onStopRecording()
                            },
                            onLongPress = {
                                isPressed = true
                                onStartRecording()
                            },
                        )
                    }
            )
        }
    }
}

@Composable
fun Circle(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    color
                )
        )
    }
}

@Preview
@Composable
fun CameraButtonPreview() {
    CameraXTheme {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CameraButton(
                onPhotoTaken = {},
                onStartRecording = {},
                onStopRecording = {}
            )
        }
    }
}