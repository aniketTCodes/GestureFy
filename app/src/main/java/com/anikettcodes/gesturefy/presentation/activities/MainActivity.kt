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
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    root(data = data){
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.Builder()
                                .scheme("https")
                                .authority("accounts.spotify.com")
                                .appendPath("authorize")
                                .appendQueryParameter("response_type","code")
                                .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                                .appendQueryParameter("scope", "streaming")
                                .appendQueryParameter("redirect_uri",BuildConfig.REDIRECT_URI)
                                .appendQueryParameter("state", UUID.randomUUID().toString())
                                .build()
                        )
                        startActivity(intent)
                    }
                }
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
    connectSpotify:()->Unit
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
        HomeScreen()
    }
    else{
        AuthorizationScreen {
            connectSpotify()
        }
    }
}