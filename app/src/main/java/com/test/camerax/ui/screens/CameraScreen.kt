package com.test.camerax.ui.screens

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.test.camerax.ui.components.CameraPreview
import com.test.camerax.ui.components.GalleryBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CameraScreen(
    onTakePhotoClicked: () -> Unit,
    onRecordVideoClicked: () -> Unit,
    controller: LifecycleCameraController,
    bitmaps: List<Bitmap>,
    isRecording: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()


    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            GalleryBottomSheet(
                bitmaps = bitmaps,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
            )

            if (isRecording) {
                Text(
                    text = "Recording...",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(16.dp, 16.dp)
                )
            }

            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                },
                modifier = Modifier.offset(8.dp, 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera"
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open Galery"
                    )
                }

                IconButton(onClick = onTakePhotoClicked) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take Photo"
                    )
                }

                IconButton(onRecordVideoClicked) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Record Video"
                    )
                }
            }
        }
    }
}