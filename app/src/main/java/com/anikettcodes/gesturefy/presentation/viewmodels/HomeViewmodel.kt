package com.anikettcodes.gesturefy.presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anikettcodes.gesturefy.domain.usecases.PlayerStateUseCase
import com.anikettcodes.gesturefy.utils.Resource

import com.spotify.protocol.types.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val playerStateUseCase: PlayerStateUseCase
) :ViewModel(){
    private val _state = mutableStateOf<HomeState>(
        HomeState(true,null,null)
    )
    val state = _state

    init {
        viewModelScope.launch {
            playerStateUseCase().collect{ resource->
                when(resource){
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = resource.message
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(
                            isLoading = true,
                            error = null,
                        )
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = null,
                            playerState = resource.data
                        )
                    }
                }
            }
        }
    }
}

data class HomeState(
    var isLoading:Boolean,
    var error: String?,
    var playerState: PlayerState?
)