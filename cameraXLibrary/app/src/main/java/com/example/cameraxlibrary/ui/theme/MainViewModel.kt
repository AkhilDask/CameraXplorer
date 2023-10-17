package com.example.cameraxlibrary.ui.theme

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel:ViewModel() {

    private val _cameraImageList = MutableStateFlow<List<Bitmap>>(emptyList())
    val imageBitmap = _cameraImageList.asStateFlow()

    fun onCapture(bitmap: Bitmap){
        _cameraImageList.value += bitmap
    }
}