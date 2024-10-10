package com.anikettcodes.gesturefy.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.presentation.destinations.Destination
import com.anikettcodes.gesturefy.presentation.screens.AuthorizationScreen
import com.anikettcodes.gesturefy.presentation.screens.HomeScreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.viewmodels.AuthorizationViewmodel
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var spotifyAppRemote:SpotifyAppRemote? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data: Uri? = intent?.data
        data?.let {
            Log.d(TAG,it.toString())
        }
        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    root(
                        data = data,
                        onLogIn = {
                            val connectionParam = ConnectionParams.Builder(BuildConfig.CLIENT_ID)
                                .setRedirectUri(BuildConfig.REDIRECT_URI)
                                .build()

                            SpotifyAppRemote.connect(this,connectionParam, object : Connector.ConnectionListener{
                                override fun onConnected(p0: SpotifyAppRemote?) {
                                    spotifyAppRemote = p0
                                    connected()
                                }

                                override fun onFailure(p0: Throwable?) {
                                    Log.e(TAG,p0?.message?:"Could not connect to spotify")
                                }
                            })
                        }
                    ){
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

    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }
    companion object{
        const val TAG = "MAIN_ACTIVITY"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun root(
    data:Uri?,
    onLogIn:()->Unit,
    connectSpotify:()->Unit,
){
    var authorizationCalled by rememberSaveable{
        mutableStateOf(false)
    }
    val navController = rememberNavController()
    val authorizationViewModel = hiltViewModel<AuthorizationViewmodel>()
    if(data != null && !authorizationCalled){
        authorizationViewModel.authorizeUser(data)
        authorizationCalled = true
    }
    if(authorizationViewModel.state.value.isLoading){
        Text(text = "Loading...")
    }
    else if(authorizationViewModel.state.value.isLoggedIn){
        onLogIn()
        HomeScreen()
    }
    else{
        AuthorizationScreen {
            connectSpotify()
        }
    }
}