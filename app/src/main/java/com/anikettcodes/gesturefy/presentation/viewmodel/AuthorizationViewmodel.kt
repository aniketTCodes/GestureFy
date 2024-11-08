package com.anikettcodes.gesturefy.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.domain.usecase.ConnectSpotifyAppRemoteUsecase
import com.anikettcodes.gesturefy.domain.usecase.SpotifyInstalledUsecase
import com.anikettcodes.gesturefy.util.Resource
import com.spotify.protocol.types.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewmodel @Inject constructor(
    private val connectSpotifyAppRemoteUsecase: ConnectSpotifyAppRemoteUsecase,
    private val spotifyInstalledUsecase: SpotifyInstalledUsecase
) : ViewModel(){
    private var _state = mutableStateOf(
        AuthState(
            isSpotifyInstalled = false,
            isLoading = true,
            isConnected = false
        )
    )

    val state = _state

    init {
        val isSpotifyInstalled = spotifyInstalledUsecase.invoke()
        if(isSpotifyInstalled is Resource.Success && isSpotifyInstalled.data == true) {
            _state.value = _state.value.copy(isSpotifyInstalled = true,)
            connectSpotifyAppRemote()
        }
        else{
            _state.value = _state.value.copy(errorMessage = isSpotifyInstalled.message, isLoading = false)
        }
    }

    private fun connectSpotifyAppRemote(){
        viewModelScope.launch {
            connectSpotifyAppRemoteUsecase().collect{
                when(it){
                    is Resource.Error -> _state.value = _state.value.copy(isLoading = false, isConnected = false, errorMessage = it.message)
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(isLoading = false, isConnected = true)
                }
            }
        }
    }

}

data class AuthState(
    val isSpotifyInstalled:Boolean,
    val isLoading:Boolean,
    val isConnected:Boolean,
    val errorMessage: String? = null
)