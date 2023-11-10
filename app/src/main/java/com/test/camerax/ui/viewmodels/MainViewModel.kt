package com.test.camerax.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _bitmaps = MutableStateFlow(emptyList<Bitmap>())
    val bitmaps = _bitmaps.asStateFlow()

    fun addPhoto(bitmap: Bitmap) {
        _bitmaps.value = _bitmaps.value + bitmap
    }

    fun startRecording() {
        _isRecording.value = true
    }

    fun stopRecording() {
        _isRecording.value = false
    }
}