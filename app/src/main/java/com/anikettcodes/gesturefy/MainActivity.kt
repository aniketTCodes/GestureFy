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
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class MainActivity : ComponentActivity() {

    val authorizationBuilder = AuthorizationRequest.Builder(clientId,AuthorizationResponse.Type.TOKEN,redirectUri)

    override fun onCreate(savedInstanceState: Bundle?) {
        authorizationBuilder.setScopes(arrayOf("app-remote-control"))
        val request = authorizationBuilder.build()
        AuthorizationClient.openLoginInBrowser(this,request)
        super.onCreate(savedInstanceState)
        setContent {
            GestureFyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }




    override fun onStop() {
        super.onStop()
    }

    private fun connected(){

    }


    companion object{
        val TAG = "MAIN_ACTIVITY"
        private val REQUEST_CODE = 1337
        val clientId = "b97db8c9499948cbb95fedcbb466b0d7"
         val redirectUri = "mkhu://hy"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GestureFyTheme {
        Greeting("Android")
    }
}