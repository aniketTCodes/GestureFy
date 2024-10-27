package com.anikettcodes.gesturefy.presentation.screens

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.anikettcodes.gesturefy.presentation.viewmodels.HomeState
import com.spotify.android.appremote.api.SpotifyAppRemote

@Composable
fun HomeScreen(
    homeState: HomeState,
    spotifyAppRemote: SpotifyAppRemote?,
    preview: PreviewView
) {
   Column {
       Text(text = homeState.trackName)
       Text(text = homeState.artistName)
       Button(onClick = {spotifyAppRemote?.playerApi?.skipNext()} ) {
           Text(text = "Next")
       }

       Button(onClick = {spotifyAppRemote?.playerApi?.skipPrevious()}) {
           Text(text = "Prev")
       }
       AndroidView(factory = {preview}, modifier = Modifier.fillMaxSize())

   } 
}
