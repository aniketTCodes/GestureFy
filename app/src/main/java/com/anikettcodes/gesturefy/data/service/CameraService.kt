package com.anikettcodes.gesturefy.data.service

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class CameraService @Inject constructor(
    private val context:Context
) {

    var cameraProvider:ProcessCameraProvider? = null
    private var frameSkippedCounter = 0
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun setUpCamera(
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProvider = suspendCancellableCoroutine {
            cameraProviderFuture.addListener(
                {
                    try{
                        it.resume(cameraProviderFuture.get()) {}
                    } catch (e:Exception){
                        it.resumeWithException(e)
                    }
                },
                ContextCompat.getMainExecutor(context)
            )
        }
    }

    fun bindImageAnalysis(
        lifeCycleOwner: LifecycleOwner,
        analyzeImage:(ImageProxy)-> Unit
    ){
        val cameraProvider = cameraProvider?:throw IllegalStateException("Camera initialization failed")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        val imageAnalysis = ImageAnalysis.Builder().setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)

            .build().apply {
                setAnalyzer(ContextCompat.getMainExecutor(context)){image->

                    if(frameSkippedCounter % 60 == 0) {
                        analyzeImage(image)
                    }
                    frameSkippedCounter++
                    image.close()
                }
            }
        cameraProvider.unbindAll()

        try{
            cameraProvider.bindToLifecycle(lifeCycleOwner,cameraSelector,imageAnalysis)
        } catch (e:Exception){
            Log.e(TAG,"Use case binding failed ${e.printStackTrace()}")
        }
    }

    companion object{
        const val TAG = "CAMERA_SERVICE"
    }


}