package com.test.camerax.ui.screens

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.camerax.extensions.CollectedEffect
import com.test.camerax.ui.components.CameraPreview
import com.test.camerax.ui.components.GalleryBottomSheet
import com.test.camerax.ui.viewmodels.CameraViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CameraScreen(
    viewModel: CameraViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val bitmaps by viewModel.bitmaps.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingError = viewModel.recordingError
    val recordingSuccess = viewModel.recordingSuccess

    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    CollectedEffect(flow = recordingSuccess) {
        Toast.makeText(
            context,
            "Video recorded successfully",
            Toast.LENGTH_LONG
        ).show()
    }

    CollectedEffect(flow = recordingError) {
        Toast.makeText(
            context,
            "Error while recording video: $it",
            Toast.LENGTH_LONG
        ).show()
    }

    CollectedEffect(flow = viewModel.cameraPermissionError) {
        Toast.makeText(
            context,
            "You need to grant camera permissions to take a photo",
            Toast.LENGTH_LONG
        ).show()
    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
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
                            showBottomSheet = true
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open Galery"
                    )
                }

                IconButton(onClick = {
                    viewModel.takePhoto(controller, context)
                }) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take Photo"
                    )
                }

                IconButton(onClick = {
                    viewModel.recordVideo(controller, context)
                }) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Record Video"
                    )
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) showBottomSheet = false

                    }
                },
                sheetState = sheetState,
            ) {
                GalleryBottomSheet(
                    bitmaps = bitmaps,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}