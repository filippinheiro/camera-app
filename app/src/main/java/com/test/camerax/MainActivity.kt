package com.test.camerax

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.test.camerax.extensions.hasPermissions
import com.test.camerax.extensions.requestRuntimePermissions
import com.test.camerax.extensions.takePhoto
import com.test.camerax.ui.screens.CameraScreen
import com.test.camerax.ui.theme.CameraXTheme
import com.test.camerax.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    private var recording: Recording? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(CAMERA_PERMISSIONS)) {
            requestRuntimePermissions(CAMERA_PERMISSIONS)
        }

        setContent {
            CameraXTheme {
                val viewModel by viewModels<MainViewModel>()
                val bitmaps by viewModel.bitmaps.collectAsState()
                val isRecording by viewModel.isRecording.collectAsState()

                val controller = remember {
                    LifecycleCameraController(this).apply {
                        setEnabledUseCases(
                            CameraController.IMAGE_CAPTURE or
                                    CameraController.VIDEO_CAPTURE
                        )
                    }
                }

                CameraScreen(
                    isRecording = isRecording,
                    controller = controller,
                    bitmaps = bitmaps,
                    onTakePhotoClicked = {
                        lifecycleScope.launch {
                            if (hasPermissions(CAMERA_PERMISSIONS)) {
                                val photo = controller.takePhoto(this@MainActivity)
                                viewModel.addPhoto(photo)
                                return@launch
                            }

                            Toast.makeText(
                                this@MainActivity,
                                "You need to grant camera permissions to take a photo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    onRecordVideoClicked = {
                        viewModel.startRecording()
                        recordVideo(controller) { error ->
                            if (error != null) {
                                recording?.close()
                                recording = null

                                Toast.makeText(
                                    this,
                                    "Error while recording video: $error",
                                    Toast.LENGTH_LONG
                                ).show()

                                viewModel.stopRecording()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Video recorded successfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                viewModel.stopRecording()
                            }
                        }
                    }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun recordVideo(
        controller: LifecycleCameraController,
        onRecordVideoStopped: (error: Throwable?) -> Unit,
    ) {
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        if (!hasPermissions(CAMERA_PERMISSIONS)) {
            return
        }

        val outputFile = File(filesDir, "my-recorded-video.mp4")
        recording = controller.startRecording(
            FileOutputOptions.Builder(outputFile).build(),
            AudioConfig.create(true),
            ContextCompat.getMainExecutor(this),
        ) { event ->
            when (event) {
                is VideoRecordEvent.Finalize -> {
                    onRecordVideoStopped(event.cause)
                }
            }
        }
    }

    companion object {
        private val CAMERA_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    }
}