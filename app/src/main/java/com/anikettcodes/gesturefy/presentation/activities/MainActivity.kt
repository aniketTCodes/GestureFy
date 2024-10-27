package com.anikettcodes.gesturefy.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.gesture.Gesture
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.data.GestureRecognizerHelper

import com.anikettcodes.gesturefy.presentation.screens.AuthorizationScreen
import com.anikettcodes.gesturefy.presentation.screens.HomeScreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.viewmodels.AuthorizationViewmodel
import com.anikettcodes.gesturefy.presentation.viewmodels.HomeViewmodel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var spotifyAppRemote:SpotifyAppRemote? = null
    private var gestureRecognizer:GestureRecognizer? = null

    private  var cameraProvider: ProcessCameraProvider? = null
    private var preview:Preview? = null
    private var imageAnalysis:ImageAnalysis? = null

    private val permissionCheckActivityResult =  registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

       if(!isGranted){
           Toast.makeText(this,"Camera permission denied!",Toast.LENGTH_LONG)
       }


    }

    override fun onStart() {
        super.onStart()

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
                .setResultListener(this::onResult)
                .setErrorListener(this::onError)
                .setRunningMode(RunningMode.LIVE_STREAM)

        val options = optionsBuilder.build()
        try{
            gestureRecognizer = GestureRecognizer.createFromOptions(applicationContext,options)
        }catch (e:Exception){
            Log.e(GestureRecognizerHelper.TAG,e.message?:"Unknown error")
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var data: Uri? = intent?.data

        setupGestureRecognizer()
        when(PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {

            }
            else -> {
                permissionCheckActivityResult.launch(Manifest.permission.CAMERA)
            }
        }

        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authorizationViewmodel = hiltViewModel<AuthorizationViewmodel>()
                    if(data != null ){
                        authorizationViewmodel.authorizeUser(data!!)
                       data = null
                    }

                    if(authorizationViewmodel.state.value.isLoading) {
                        Text(text = "Loading")
                    }
                    else if(authorizationViewmodel.state.value.isLoggedIn){

                        val homeViewmodel = hiltViewModel<HomeViewmodel>()
                        //setup spotify remote
                        initializeSpotifyAppRemote(homeViewmodel)


                        preview = Preview.Builder().build()

                        val previewView = remember {
                            PreviewView(this)
                        }
                        setupCamera(previewView)
                        preview?.setSurfaceProvider(previewView.surfaceProvider)
                        HomeScreen(homeState = homeViewmodel.state.value, spotifyAppRemote = spotifyAppRemote,previewView)
                    }
                    else   {
                        AuthorizationScreen {
                            val authorizationBuilder = AuthorizationRequest.Builder(BuildConfig.CLIENT_ID,AuthorizationResponse.Type.CODE,BuildConfig.REDIRECT_URI)
                            authorizationBuilder.setScopes(
                                arrayOf("app-remote-control")
                            )
                            AuthorizationClient.openLoginInBrowser(this,authorizationBuilder.build())
                        }
                    }

                }
            }
        }
    }


    private fun initializeSpotifyAppRemote(viewmodel: HomeViewmodel){
        SpotifyAppRemote.connect(this, connectionParams,object:Connector.ConnectionListener {
            override fun onConnected(p0: SpotifyAppRemote?) {
                spotifyAppRemote = p0
                spotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
                    viewmodel.updateTrackState(it.track.name,it.track.artist.name)
                }
            }

            override fun onFailure(p0: Throwable?) {
                Log.e(TAG,p0?.message?:"Unknown error while setting up Spotify app remote")
            }

        })
    }

    private fun setupCamera( previewView: PreviewView){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(previewView)

            }, ContextCompat.getMainExecutor(applicationContext)
        )
    }

    private fun bindCameraUseCases( previewView: PreviewView){
        val cameraProvider = cameraProvider?:throw IllegalStateException("Camera initialization failed")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        imageAnalysis = ImageAnalysis.Builder().setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetRotation(previewView.display.rotation)
            .build().apply {
                setAnalyzer(ContextCompat.getMainExecutor(applicationContext)){image->
                    recognizerGesture(image)
                    image.close()
                }
            }
        cameraProvider.unbindAll()

        try{
            cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageAnalysis)
        } catch (e:Exception){
            Log.e(TAG,"Use case binding failed ${e.printStackTrace()}")
        }
    }



    companion object{
        const val TAG = "MAIN_ACTIVITY"
        val connectionParams: ConnectionParams = ConnectionParams.Builder(BuildConfig.CLIENT_ID)
            .setRedirectUri(BuildConfig.REDIRECT_URI)
            .build()

    }

    private fun recognizerGesture(imageProxy: ImageProxy){
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
     private fun onResult(result: GestureRecognizerResult, mpImage: MPImage) {
         if (result.gestures().isEmpty()){
            Log.e(TAG,"No gestures detected")
        }
        else{
        result.gestures().forEach {
            Log.d(TAG,it[0].categoryName())
        }}
    }


     private fun onError(runtimeException: RuntimeException?) {
        Log.e(TAG,runtimeException?.message?:"Model error")
    }
}

class GesturefyImageAnalyzer(val gestureRecognizerHelper:GestureRecognizerHelper):ImageAnalysis.Analyzer{
    override fun analyze(image: ImageProxy) {
        gestureRecognizerHelper.recognizerGesture(image)
    }

}