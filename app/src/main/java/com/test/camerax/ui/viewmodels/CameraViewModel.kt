package com.test.camerax.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.camerax.MainActivity
import com.test.camerax.exceptions.CameraException
import com.test.camerax.extensions.hasPermissions
import com.test.camerax.extensions.takePhoto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class CameraViewModel : ViewModel() {
    private var recording: Recording? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _bitmaps = MutableStateFlow(emptyList<Bitmap>())
    val bitmaps = _bitmaps.asStateFlow()

    private val _recordingError = MutableSharedFlow<Throwable>()
    val recordingError = _recordingError.asSharedFlow()

    private val _recordingSuccess = MutableSharedFlow<Unit>()
    val recordingSuccess = _recordingSuccess.asSharedFlow()

    private val _cameraPermissionError = MutableSharedFlow<Unit>()
    val cameraPermissionError = _cameraPermissionError.asSharedFlow()

    fun takePhoto(controller: LifecycleCameraController, context: Context) {
        viewModelScope.launch {
            if (context.hasPermissions(MainActivity.CAMERA_PERMISSIONS)) {
                val photo = controller.takePhoto(context)
                _bitmaps.value = _bitmaps.value + photo
                return@launch
            }

            _cameraPermissionError.emit(Unit)
        }
    }

    fun recordVideo(controller: LifecycleCameraController, context: Context) {
        _isRecording.value = true

        viewModelScope.launch {
            try {
                recordVideo(context, controller)
                    .getOrThrow()

                closeRecording()
                _isRecording.value = false
                _recordingSuccess.emit(Unit)
            } catch (ex: SecurityException) {
                closeRecording()
                _cameraPermissionError.emit(Unit)
                _isRecording.value = false
            } catch (ex: CameraException) {
                closeRecording()
                _recordingError.emit(ex)
                _isRecording.value = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun recordVideo(
        context: Context,
        controller: LifecycleCameraController,
    ): Result<Unit> {
        if (recording != null) {
            return Result.success(Unit)
        }

        if (!context.hasPermissions(MainActivity.CAMERA_PERMISSIONS)) {
            return Result.failure(SecurityException("Camera permissions not granted"))
        }

        return suspendCancellableCoroutine {
            val outputFile = File(context.filesDir, "my-recorded-video.mp4")
            recording = controller.startRecording(
                FileOutputOptions.Builder(outputFile).build(),
                AudioConfig.create(true),
                ContextCompat.getMainExecutor(context),
            ) { event ->
                when (event) {
                    is VideoRecordEvent.Finalize -> {
                        if (event.cause != null) {
                            it.resume(Result.failure(CameraException(cause = event.cause)))
                        }

                        if (it.isActive) {
                            it.resume(Result.success(Unit))
                        }
                    }
                }
            }
        }
    }

    private fun closeRecording() {
        recording?.close()
        recording = null
    }
}