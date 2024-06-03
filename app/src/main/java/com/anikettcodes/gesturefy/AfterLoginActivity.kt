package com.anikettcodes.gesturefy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.anikettcodes.gesturefy.ui.theme.GestureFyTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.Connector.ConnectionListener
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.spotify.sdk.android.auth.AuthorizationResponse

class AfterLoginActivity : ComponentActivity() {

    var spotifyAppRemote:SpotifyAppRemote? = null


    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track:Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.data;
        uri?.let {
            val response = AuthorizationResponse.fromUri(it)

            when(response.type){
                AuthorizationResponse.Type.TOKEN->{
                    Log.d(MainActivity.TAG,"Success")
                    val connectionParams = ConnectionParams.Builder(MainActivity.clientId).setRedirectUri(MainActivity.redirectUri).build()
                    SpotifyAppRemote.connect(this,connectionParams,object : Connector.ConnectionListener {
                        override fun onConnected(p0: SpotifyAppRemote?) {
                            spotifyAppRemote = p0
                            connected()
                        }

                        override fun onFailure(p0: Throwable?) {
                            Log.e(MainActivity.TAG,"Could not connect")
                        }
                    })
                }
                else -> {
                    Log.e(MainActivity.TAG,response.type.toString())
                }
            }
        }
        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2("After Login Activity")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    GestureFyTheme {
        Greeting2("Android")
    }
}