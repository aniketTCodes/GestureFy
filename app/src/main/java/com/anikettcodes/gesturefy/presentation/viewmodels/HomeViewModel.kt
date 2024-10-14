package com.anikettcodes.gesturefy.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor() :ViewModel() {
    private val _state = mutableStateOf<HomeState>(
        HomeState(
            "unknown",
            "unknown"
        )
    )

    val state = _state

    fun updateTrackState(trackName: String?,artistName: String?){
        _state.value = _state.value.copy(trackName = trackName?:"Unknown",artistName = artistName?:"Unknown")
    }
}

data class HomeState(
    val trackName:String,
    val artistName:String,
)