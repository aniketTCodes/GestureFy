package com.anikettcodes.gesturefy.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.anikettcodes.gesturefy.presentation.viewmodels.HomeState
import com.spotify.android.appremote.api.SpotifyAppRemote

@Composable
fun HomeScreen(
    homeState: HomeState,
    spotifyAppRemote: SpotifyAppRemote?
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
   } 
}