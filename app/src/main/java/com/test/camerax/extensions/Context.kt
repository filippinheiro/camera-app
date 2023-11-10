package com.test.camerax.extensions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal fun ComponentActivity.requestRuntimePermissions(permissions: Array<out String>) {
    ActivityCompat.requestPermissions(this, permissions, 0)
}

internal fun Context.hasPermissions(permissions: Array<out String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }
}