package com.anikettcodes.gesturefy.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.anikettcodes.gesturefy.presentation.activities.MainActivity
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult

class GestureRecognizerHelper(
    private val context:Context,
    private val activity: MainActivity
){
    private var gestureRecognizer:GestureRecognizer? = null

    init {
        setupGestureRecognizer()
    }

    private fun setupGestureRecognizer(){
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("saved_model.task").setDelegate(
            Delegate.CPU)
        val baseOptions = baseOptionsBuilder.build()

        val optionsBuilder =
            GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinHandDetectionConfidence(0.5F)
                .setMinTrackingConfidence(0.5F)
                .setMinHandPresenceConfidence(0.5F)

                .setRunningMode(RunningMode.LIVE_STREAM)

        val options = optionsBuilder.build()
        try{
            gestureRecognizer = GestureRecognizer.createFromOptions(context,options)
        }catch (e:Exception){
            Log.e(TAG,e.message?:"Unknown error")
        }
    }



     fun recognizerGesture(imageProxy: ImageProxy){
         if (gestureRecognizer == null) setupGestureRecognizer()


        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,imageProxy.height,Bitmap.Config.ARGB_8888
        )
        imageProxy.use { bitmapBuffer.copyPixelsToBuffer(imageProxy.planes[0].buffer) }


        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // flip image since we only support front camera
            postScale(
                -1f, 1f, imageProxy.width.toFloat(), imageProxy.height.toFloat()
            )
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            matrix,
            true
        )

        // Convert the input Bitmap object to an MPImage object to r
        val mpImage = BitmapImageBuilder(rotatedBitmap).build()
        gestureRecognizer?.recognizeAsync(mpImage,SystemClock.uptimeMillis())
    }

    companion object{
        const val TAG = "GESTURE_RECOGNIZER_HELPER"
    }
}
