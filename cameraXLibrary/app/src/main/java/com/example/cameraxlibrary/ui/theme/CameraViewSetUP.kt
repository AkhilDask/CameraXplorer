package com.example.cameraxlibrary.ui.theme

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraViewSetup(
    cameraController:LifecycleCameraController,
    modifier: Modifier = Modifier
){
     val lifeCycleOwner = LocalLifecycleOwner.current

    AndroidView(factory = {
        PreviewView(it).apply {
            this.controller = cameraController
            cameraController.bindToLifecycle(lifeCycleOwner)
        }
    }, modifier = modifier)
}