package com.anikettcodes.gesturefy.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme


@Composable
fun AuthorizationScreen( onClick:()->Unit){
    Column {
        Button(onClick = onClick) {
           Text(text = "Connect Spotify")
        }
    }
}

@Preview
@Composable
fun AuthorizationScreenPreview(){
    GestureFyTheme {
        AuthorizationScreen(onClick = {})
    }
}
