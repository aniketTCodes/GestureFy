package com.anikettcodes.gesturefy.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.presentation.screens.AuthorizationScreen
import com.anikettcodes.gesturefy.presentation.screens.HomeScreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.viewmodels.AuthorizationViewmodel
import com.anikettcodes.gesturefy.presentation.viewmodels.HomeViewmodel
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
    private val permissionCheckActivityResult =  registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

       if(!isGranted){
           Toast.makeText(this,"Camera permission denied!",Toast.LENGTH_LONG)
       }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var data: Uri? = intent?.data

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
                        HomeScreen(homeState = homeViewmodel.state.value, spotifyAppRemote = spotifyAppRemote)
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

    companion object{
        const val TAG = "MAIN_ACTIVITY"
        val connectionParams: ConnectionParams = ConnectionParams.Builder(BuildConfig.CLIENT_ID)
            .setRedirectUri(BuildConfig.REDIRECT_URI)
            .build()
    }
}
