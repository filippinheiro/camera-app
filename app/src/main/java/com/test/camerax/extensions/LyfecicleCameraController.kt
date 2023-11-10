package com.test.camerax.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun LifecycleCameraController.takePhoto(context: Context): Bitmap =
    suspendCoroutine {
        takePicture(
            ContextCompat.getMainExecutor(context),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val finishedImageMatrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())

                        if (this@takePhoto.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                            postScale(-1f, 1f)
                        }
                    }

                    val finishedImageBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        finishedImageMatrix,
                        true
                    )

                    it.resumeWith(Result.success(finishedImageBitmap))
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    it.resumeWithException(exception)
                }
            }
        )
    }