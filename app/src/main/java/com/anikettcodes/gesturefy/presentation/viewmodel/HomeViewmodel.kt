package com.anikettcodes.gesturefy.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anikettcodes.gesturefy.domain.usecase.PlaybackControlUsecase
import com.anikettcodes.gesturefy.domain.usecase.PlayerStateUsecase
import com.anikettcodes.gesturefy.presentation.ui.theme.BackgroundGreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFySurface
import com.anikettcodes.gesturefy.util.Resource
import com.spotify.protocol.types.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val playerStateUsecase: PlayerStateUsecase,
    private val playbackControlUsecase: PlaybackControlUsecase
) : ViewModel() {
    private val _state = mutableStateOf(
        HomeState(null,null, BackgroundGreen)
    )

    val state = _state
    init {
        viewModelScope.launch {
            playerStateUsecase().collect{
                when(it){
                    is Resource.Error -> _state.value = _state.value.copy(errorMessage = it.message)
                    is Resource.Loading -> TODO()
                    is Resource.Success -> _state.value = _state.value.copy(playerState = it.data)
                }
            }
        }
    }
}

data class HomeState(
    val playerState:PlayerState?,
    val errorMessage:String?,
    val backgroundColor:Color
)