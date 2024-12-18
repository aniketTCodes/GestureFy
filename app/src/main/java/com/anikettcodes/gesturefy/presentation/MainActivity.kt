package com.anikettcodes.gesturefy.presentation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.presentation.screen.AuthorizationScreen
import com.anikettcodes.gesturefy.presentation.screen.HomeScreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.ui.theme.SpotifyGreen
import com.anikettcodes.gesturefy.presentation.viewmodel.AuthorizationViewmodel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.auth.api.Auth
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Root()
                }
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    private fun Root() {
        val authorizationViewmodel = hiltViewModel<AuthorizationViewmodel>()
        val state = authorizationViewmodel.state.value
        if (state.isLoading) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            )
            {
                CircularProgressIndicator(color = SpotifyGreen)
            }
        } else if (state.isConnected)
            HomeScreen(this)
        else{
            val snackbarHostState = remember { SnackbarHostState()}
            Scaffold(
                contentWindowInsets = WindowInsets(top = 0, bottom = WindowInsets.systemBars.getBottom(
                    LocalDensity.current)),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState){data->
                    Snackbar(
                        snackbarData = data,
                        contentColor = Color.White,
                        containerColor = Color.Red
                    )
                } },
            ) {
                Column(
                    modifier = Modifier.padding(it)
                ) {
                    LaunchedEffect(state.errorMessage) {
                        if(state.errorMessage != null){
                            snackbarHostState.showSnackbar(message = state.errorMessage)
                        }
                    }
                    AuthorizationScreen(
                        state.isSpotifyInstalled
                    ) { if (state.isSpotifyInstalled) connectToSpotify() else getSpotify() }
                }
            }
        }
    }
    private fun getSpotify(){
        val getSpotifyIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.spotify.music")
            setPackage("com.android.vending")
        }
        startActivity(getSpotifyIntent)
    }

    private fun connectToSpotify(){
        val authorizationRequest = com.spotify.sdk.android.auth.AuthorizationRequest.Builder(
            BuildConfig.CLIENT_ID,AuthorizationResponse.Type.CODE,BuildConfig.REDIRECT_URI
        ).setScopes(arrayOf("app-remote-control"))
            .build()

        AuthorizationClient.openLoginInBrowser(this,authorizationRequest)
    }
}
