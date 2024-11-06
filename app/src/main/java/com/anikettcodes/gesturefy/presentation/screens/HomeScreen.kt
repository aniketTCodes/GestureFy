package com.anikettcodes.gesturefy.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.anikettcodes.gesturefy.presentation.viewmodels.HomeViewmodel
import kotlinx.coroutines.delay
import java.time.Duration

@Composable
fun HomeScreen() {
    
    val homeViewmodel = hiltViewModel<HomeViewmodel>()
    val state = homeViewmodel.state.value
    
    if (state.isLoading){
        Column {
            CircularProgressIndicator()
        }
    }
    else if(state.playerState != null){
        val playerState = state.playerState!!
        var playbackPosition by rememberSaveable {
            mutableStateOf(0)
        }

        LaunchedEffect(key1 = playerState.isPaused, key2 = playerState.playbackPosition) {
            playbackPosition = (playerState.playbackPosition/1000).toInt()
            while (!playerState.isPaused){
                delay(1000)
                playbackPosition++

            }
        }
        Column {
            Text(text = playerState.track.name)
            Text(text = playerState.track.artist.name)
            Text(text = playbackPosition.toString())
            Text(text = (playerState.playbackPosition/1000).toInt().toString())
            Text(text = playerState.isPaused.toString())
        }
    }
}