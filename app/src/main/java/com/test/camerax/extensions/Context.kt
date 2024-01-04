package com.test.camerax.extensions

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal fun ComponentActivity.requestCameraAndAudioRuntimePermissions() {
    ActivityCompat.requestPermissions(this, CAMERA_AND_AUDIO_PERMISSIONS, 0)
}

internal fun Context.hasCameraAndAudioPermissions(): Boolean {
    return CAMERA_AND_AUDIO_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private val CAMERA_AND_AUDIO_PERMISSIONS = arrayOf(
    CAMERA,
    RECORD_AUDIO
)