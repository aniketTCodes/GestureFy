package com.anikettcodes.gesturefy.presentation.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import com.anikettcodes.gesturefy.data.repository.PlayerOperation
import com.anikettcodes.gesturefy.domain.usecase.AlbumArtUseCase
import com.anikettcodes.gesturefy.domain.usecase.GestureRecognizerUsecase
import com.anikettcodes.gesturefy.domain.usecase.PlaybackControlUsecase
import com.anikettcodes.gesturefy.domain.usecase.PlayerStateUsecase
import com.anikettcodes.gesturefy.presentation.ui.theme.BackgroundGreen
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFySurface
import com.anikettcodes.gesturefy.util.Resource
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Response
import javax.inject.Inject


@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val playerStateUsecase: PlayerStateUsecase,
    private val playbackControlUsecase: PlaybackControlUsecase,
    private val albumArtUseCase: AlbumArtUseCase,
    private val gestureRecognizerUsecase: GestureRecognizerUsecase
) : ViewModel() {
    private val _state = mutableStateOf(
        HomeState(null,null,null, BackgroundGreen)
    )
    val state = _state
    init {
        viewModelScope.launch {
            playerStateUsecase().collect{ it ->
                when(it){
                    is Resource.Error -> _state.value = _state.value.copy(errorMessage = it.message)
                    is Resource.Loading -> TODO()
                    is Resource.Success -> {
                        if(it.data!!.track != null){
                            if(_state.value.playerState == null || _state.value.playerState!!.track == null ||
                                _state.value.playerState!!.track.name != it.data.track.name){
                                updateAlbumArt(it.data.track.imageUri)
                            }
                        } else{
                            updateAlbumArt(null)
                        }
                        state.value = _state.value.copy(playerState = it.data)
                    }
                }
            }
        }
    }

     fun startGestureRecognizer(lifecycleOwner: LifecycleOwner){
       viewModelScope.launch {
           gestureRecognizerUsecase(lifecycleOwner)
       }
    }


    private suspend fun updateAlbumArt(uri: ImageUri?){
        if(uri == null){
            _state.value = _state.value.copy(albumArt = null)
            return
        }
        albumArtUseCase(uri).collect{
            when(it){
                is Resource.Error -> {
                    _state.value = _state.value.copy(errorMessage = it.message)
                }
                is Resource.Loading -> {}
                is Resource.Success -> {
                    updateBackgroundColor(it.data!!)
                    _state.value = _state.value.copy(albumArt = it.data)
                }
            }
        }
    }

    private fun updateBackgroundColor(bitmap: Bitmap){
        val palette = Palette.Builder(bitmap).generate()
        val swatch  = palette.vibrantSwatch
        if(swatch != null){
            _state.value = _state.value.copy(backgroundColor = Color(swatch.rgb))
        }
        else{
            _state.value = _state.value.copy(backgroundColor = BackgroundGreen)
        }

    }

    fun performOperation(operation: PlayerOperation){
         playbackControlUsecase(operation)
    }

    fun seekTo(seekTo:Long){
        val seekResult = playbackControlUsecase.seekTo(seekTo)
        if(seekResult is Resource.Error){
            _state.value = _state.value.copy(errorMessage = seekResult.message)
        }
    }
}

data class HomeState(
    val playerState:PlayerState?,
    val albumArt:Bitmap?,
    val errorMessage:String?,
    val backgroundColor:Color
)