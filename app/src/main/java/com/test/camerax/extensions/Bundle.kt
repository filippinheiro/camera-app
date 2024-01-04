package com.test.camerax.extensions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

internal inline fun <reified T : Parcelable> Bundle.getParcelableSafe(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(name, T::class.java)
    } else {
        getParcelable(name)
    }
}