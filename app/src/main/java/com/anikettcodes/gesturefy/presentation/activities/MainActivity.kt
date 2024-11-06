package com.anikettcodes.gesturefy.presentation.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
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
import com.anikettcodes.gesturefy.presentation.viewmodels.HomeViewmodel
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var data: Uri? = intent?.data
        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authorizationViewmodel = hiltViewModel<AuthorizationViewmodel>()
                    if(data != null){
                        authorizationViewmodel.authorizeUser(data!!)
                        data = null
                    }
                    if(authorizationViewmodel.state.value.isLoading){
                        Column {
                            Text("Loading")
                        }
                    }
                    else if(authorizationViewmodel.state.value.isLoggedIn){
                      HomeScreen()
                    }
                    else{
                        AuthorizationScreen {
                            AuthorizationClient.openLoginInBrowser(
                                this,
                                AuthorizationRequest.Builder(
                                    BuildConfig.CLIENT_ID,
                                    AuthorizationResponse.Type.CODE,
                                    BuildConfig.REDIRECT_URI
                                )
                                    .setScopes(arrayOf("app-remote-control"))
                                    .build()
                            )
                        }
                    }

                }
            }
        }
    }

    companion object{
        const val TAG = "MAIN_ACTIVITY"
    }
}

