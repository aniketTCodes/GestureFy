package com.anikettcodes.gesturefy.presentation

import android.content.Intent
import android.net.Uri
import android.net.Uri.Builder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.presentation.screens.AuthorizationScreen
import com.anikettcodes.gesturefy.presentation.screens.HomeScreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.viewmodel.AuthorizationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        data?.let {
            Log.d("Main",it.toString())
        }

        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Root(
                        onAuthorize = {
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
                                    .appendQueryParameter("state",UUID.randomUUID().toString())
                                    .build()
                            )
                            startActivity(intent)
                        },
                        data = data
                    )
                }
            }
        }
    }


}

@Composable
fun Root(
    onAuthorize:()->Unit,
    data:Uri?
){
    val authorizationViewModel = hiltViewModel<AuthorizationViewModel>()
    data?.let {

    }
    authorizationViewModel.state.value.let {
        if(it.isLoading) {
            Column {
                Text(text = "Loading...")
            }
        } else {

            if(it.isLoggedIn){
                HomeScreen()
            } else {
                AuthorizationScreen(
                    authorizationViewModel,
                    onAuthorize = onAuthorize
                )
            }

        }
    }
}