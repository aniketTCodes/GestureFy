package com.anikettcodes.gesturefy.data.repository

import android.util.Log
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.anikettcodes.gesturefy.data.service.CameraService
import com.anikettcodes.gesturefy.data.service.GestureRecognizerService
import com.anikettcodes.gesturefy.data.service.SpotifyAppRemoteService
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import javax.inject.Inject

class GestureFyRepository @Inject constructor(
    private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository,
    private val gestureRecognizerService: GestureRecognizerService,
    private val cameraService: CameraService
) {

    init {
        gestureRecognizerService.setGestureResultListener(this::onResult)
        gestureRecognizerService.setErrorListener(this::onError)
    }

    suspend fun startGestureRecognition(
        lifeCycleOwner: LifecycleOwner
    ){
        try{
            Log.d(TAG,"starting camera setup")
            cameraService.setUpCamera()
            gestureRecognizerService.setupGestureRecognizer()
            cameraService.bindImageAnalysis(lifeCycleOwner){
                gestureRecognizerService.recognizerGesture(it)
            }
        } catch (e:Exception){
            Log.e(TAG,e.message?:"An error occurred")
            throw e
        }
    }

    private fun onResult(result:GestureRecognizerResult,mpImage: MPImage){
        if(result.gestures().isNotEmpty() || result.gestures()[0].isNotEmpty()){

            result.gestures().forEach {
                Log.d(TAG, it[0].categoryName())
            }

            val category = result.gestures()[0][0].categoryName()
            val score = result.gestures()[0][0].score()

            category?.let {
               if(score >= 0.6){

                   when(it){
                       "next" -> spotifyAppRemoteRepository.performPlayerOperation(PlayerOperation.NEXT)
                       "prev" -> spotifyAppRemoteRepository.performPlayerOperation(PlayerOperation.PREV)
                       "play pause" -> spotifyAppRemoteRepository.performPlayerOperation(PlayerOperation.PLAY)
                        else -> {}
                   }


               }
            }

        }
    }

    private fun onError(e:RuntimeException){

    }

    companion object {
        const val TAG = "GESTUREFY_REPOSITORY"
    }
}