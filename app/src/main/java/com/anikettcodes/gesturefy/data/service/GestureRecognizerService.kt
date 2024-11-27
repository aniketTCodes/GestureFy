package com.anikettcodes.gesturefy.data.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import javax.inject.Inject

class GestureRecognizerService @Inject constructor(
    private val context: Context,
){
    private var optionBuilder: GestureRecognizer.GestureRecognizerOptions.Builder = GestureRecognizer.GestureRecognizerOptions.builder()
    private var gestureRecognizer:GestureRecognizer? = null

    fun setGestureResultListener(
        resultListener:(GestureRecognizerResult,MPImage)->Unit
    ){
        optionBuilder.setResultListener(resultListener)
    }

    fun setErrorListener(
        errorListener:(RuntimeException)->Unit
    ){
        optionBuilder.setErrorListener(errorListener)
    }

    fun setupGestureRecognizer(){
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("saved_model.task").setDelegate(
            Delegate.CPU)
        val baseOptions = baseOptionsBuilder.build()
        optionBuilder.setBaseOptions(baseOptions)
            .setMinHandDetectionConfidence(0.5F)
            .setMinTrackingConfidence(0.5F)
            .setMinHandPresenceConfidence(0.5F)
            .setRunningMode(RunningMode.LIVE_STREAM)

        val options = optionBuilder.build()
        try{
            gestureRecognizer = GestureRecognizer.createFromOptions(context,options)
        }catch (e:Exception){
            Log.e(TAG,e.message?:"Unknown error")
        }
    }

    fun recognizerGesture(imageProxy: ImageProxy){
        val frameTime = SystemClock.uptimeMillis()

        // Copy out RGB bits from the frame to a bitmap buffer
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
        )
        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }


        val matrix = Matrix().apply {
            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // flip image since we only support front camera
            postScale(
                -1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat()
            )
        }

        // Rotate bitmap to match what our model expects
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        // Convert the input Bitmap object to an MPImage object to run inference
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        gestureRecognizer?.recognizeAsync(mpImage,frameTime)
    }
    
    companion object {
        const val TAG = "GESTURE_RECOGNIZER_SERVICE"
    }

}
