package com.test.camerax.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal fun CameraPreview(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                controller.bindToLifecycle(lifecycleOwner)
            }
        }
    )
}