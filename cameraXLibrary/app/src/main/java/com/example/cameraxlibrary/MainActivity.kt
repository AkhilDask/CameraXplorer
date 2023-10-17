package com.example.cameraxlibrary


import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cameraxlibrary.ui.theme.CameraViewSetup
import com.example.cameraxlibrary.ui.theme.CameraXLibraryTheme
import com.example.cameraxlibrary.ui.theme.GalleryViewSetUp
import com.example.cameraxlibrary.ui.theme.MainViewModel
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher:ActivityResultLauncher<Array<String>>
    private var isCameraPermissionGranted = false
    private var isRecordAudioPermissionGranted = false

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions ->
            isCameraPermissionGranted = permissions[android.Manifest.permission.CAMERA] ?: isCameraPermissionGranted
            isRecordAudioPermissionGranted = permissions[android.Manifest.permission.RECORD_AUDIO] ?: isRecordAudioPermissionGranted
        }

        havePermission()


        setContent {

            val scaffoldState = rememberBottomSheetScaffoldState()
            val cameraController = LifecycleCameraController(applicationContext).apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
                )
            }

            val viewModel = viewModel<MainViewModel>()
            val scope = rememberCoroutineScope()

            val imageBitmaps by viewModel.imageBitmap.collectAsState()
            CameraXLibraryTheme {

             BottomSheetScaffold(scaffoldState = scaffoldState,
                 sheetPeekHeight = 0.dp,sheetContent ={
                GalleryViewSetUp(list = imageBitmaps, modifier = Modifier.fillMaxWidth() )
             } ){
                 paddingValues -> Box(modifier = Modifier
                 .fillMaxSize()
                 .padding(paddingValues)) {
                 CameraViewSetup(
                     cameraController = cameraController,
                     modifier = Modifier.fillMaxSize()
                 )

                 IconButton(onClick = {
                     cameraController.cameraSelector =
                         if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                             CameraSelector.DEFAULT_FRONT_CAMERA
                         } else
                             CameraSelector.DEFAULT_BACK_CAMERA
                 }, modifier = Modifier.offset(20.dp, 20.dp)) {
                     Icon(
                         imageVector = Icons.Default.SwitchCamera,
                         contentDescription = "switch camera"
                     )
                 }


                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .align(Alignment.BottomCenter)
                         .padding(10.dp),
                     horizontalArrangement = Arrangement.SpaceAround,
                 ) {

                     IconButton(onClick = {
                         scope.launch {
                             scaffoldState.bottomSheetState.expand()
                         }
                     }) {
                         Icon(
                             imageVector = Icons.Default.BrowseGallery,
                             contentDescription = "Gallery"
                         )
                     }

                     IconButton(onClick = {
                         captureImage(
                             cameraController,
                             viewModel::onCapture
                         )
                     }) {
                         Icon(
                             imageVector = Icons.Default.PhotoCamera,
                             contentDescription = "capture image"
                         )
                     }


                 }
             }

             }
            }
        }
    }

    private fun captureImage(controller: LifecycleCameraController,onCaptureImage: (Bitmap) -> Unit){
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object: OnImageCapturedCallback(){
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val matrix = Matrix().apply {
                        postRotate(image.imageInfo.rotationDegrees.toFloat())
                    }

                    val rotatedImageBitmap = Bitmap.createBitmap(
                        image.toBitmap(),
                        0,
                        0,
                        image.width,
                        image.height,
                        matrix,
                        true
                    )
                    onCaptureImage(rotatedImageBitmap)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Toast.makeText(applicationContext,"Image not Captured",Toast.LENGTH_SHORT).show()

                }
            }

        )
    }


    private fun havePermission(){
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(applicationContext,
            android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        isRecordAudioPermissionGranted = ContextCompat.checkSelfPermission(applicationContext,
            android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        val permissionRequest:MutableList<String> = ArrayList()

        if (!isCameraPermissionGranted){
            permissionRequest.add(android.Manifest.permission.CAMERA)
        }

        if (!isRecordAudioPermissionGranted){
            permissionRequest.add(android.Manifest.permission.RECORD_AUDIO)
        }

        if (permissionRequest.isNotEmpty()){
            requestPermissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }


}

