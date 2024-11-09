package com.anikettcodes.gesturefy.presentation.screen

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anikettcodes.gesturefy.presentation.ui.theme.SpotifyGreen
import com.anikettcodes.gesturefy.R
import com.anikettcodes.gesturefy.presentation.ui.theme.BackgroundGreen
import com.anikettcodes.gesturefy.util.Resource
import okhttp3.internal.wait

@Composable
fun AuthorizationScreen(
    isSpotifyInstalled:Boolean,
    onClick:()->Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.0F to SpotifyGreen,
                0.72F to Color(0X00C4C4C4),
            ),
            center = Offset(0F,0F),
            radius = 1000F
        )
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f)
                .background(brush = brush)

            )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            
            Text(text = "Get moving,", fontSize = 42.sp, fontWeight = FontWeight.Bold)
            Text(text = "Stay grooving", fontSize = 42.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Unlock a new way to control Spotify with gestures, Connect your spotify to start",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = SpotifyGreen,
                        shape = RoundedCornerShape(size = 50.dp)
                    ),
                onClick = onClick)
            {
               Row (
                   verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier.padding(2.dp)
               ){
                  Image(
                      painter = painterResource(id = R.drawable.spotify_icon),
                      contentDescription = "Spotify logo",
                      modifier = Modifier.size(50.dp)
                  )
                   Spacer(modifier = Modifier.width(10.dp))
                   val buttonTextSize = 24.sp
                   val buttonText = if(isSpotifyInstalled){
                       buildAnnotatedString {
                           withStyle(
                               style = SpanStyle(color = Color.White, fontSize = buttonTextSize, fontWeight = FontWeight.Normal)
                           ){
                               append("Connect to ")
                           }
                           withStyle(
                               style = SpanStyle(fontSize = buttonTextSize, fontWeight = FontWeight.Normal)
                           ){
                               append("Spotify")
                           }
                       }
                       
                   } else {
                        buildAnnotatedString {
                           withStyle(
                               style = SpanStyle(color = Color.White, fontSize = buttonTextSize,fontWeight = FontWeight.Normal)
                           ){
                               append("Get ")
                           }
                           withStyle(style = SpanStyle(fontSize = buttonTextSize,fontWeight = FontWeight.Normal)){
                               append("Spotify ")
                           }
                           withStyle(
                               style = SpanStyle(color = Color.White, fontSize = buttonTextSize,fontWeight = FontWeight.Normal)
                           ){
                               append("Free")
                           }
                       }
                       
                   }
                   Text(text = buttonText)
               } 
            }
        }

    }
}

@Preview(showBackground = false)
@Composable
fun AuthorizationScreenPreview(){
    AuthorizationScreen(isSpotifyInstalled = true){

    }
}