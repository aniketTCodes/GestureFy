package com.anikettcodes.gesturefy.presentation.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.anikettcodes.gesturefy.presentation.viewmodel.HomeViewmodel
import com.anikettcodes.gesturefy.R
import com.anikettcodes.gesturefy.data.repository.PlayerOperation
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.ui.theme.SpotifyGreen
import com.anikettcodes.gesturefy.presentation.viewmodel.HomeState
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerRestrictions
import com.spotify.protocol.types.Repeat
import com.spotify.protocol.types.Shuffle
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeScreen(){
    val homeViewmodel = hiltViewModel<HomeViewmodel>()
    val state = homeViewmodel.state.value
    val snackbarHostState = remember {SnackbarHostState()}
    Scaffold(
        contentWindowInsets = WindowInsets(top = 0, bottom = 0),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData = snackbarData, containerColor = Color.Red, contentColor = Color.White)
            }
        }
    ) { it ->
        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let{
                snackbarHostState.showSnackbar(message = state.errorMessage)
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(it)
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            state.backgroundColor,
                            Color(0xFF191414),
                        ),
                    )
                )
                ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .border(
                                BorderStroke(1.dp, color = Color(0x25FFFFFF)),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .blur(
                                radius = 28.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(
                                        Color(0x12FFFFFF),
                                        Color(0x20FFFFFF),
                                        Color(0x9FFFFFFF)
                                    ),
                                    radius = 2200f,
                                    center = Offset.Infinite
                                )
                            )
                            .padding(4.dp)

                    ){

                        if(state.playerState?.track != null){
                            var playBackPosition by rememberSaveable {
                                mutableIntStateOf(0)
                            }
                            LaunchedEffect(
                                key1 = state.playerState.isPaused,
                                key2 = state.playerState.playbackPosition
                            ) {
                                playBackPosition = (state.playerState.playbackPosition / 1000).toInt()
                                while (!state.playerState.isPaused) {
                                    delay(1000)
                                    playBackPosition++
                                }
                            }

                            GesturefyPlayer(
                                trackName = state.playerState.track.name,
                                artistName = state.playerState.track.artist.name,
                                trackLength = state.playerState.track.duration,
                                playbackPosition = playBackPosition,
                                isShuffleOn = state.playerState.playbackOptions.isShuffling,
                                isRepeatOn = state.playerState.playbackOptions.repeatMode,
                                isPaused = state.playerState.isPaused,
                                albumArt = state.albumArt,
                                playerRestrictions = state.playerState.playbackRestrictions,
                                onSeek = {
                                    playBackPosition = it
                                },
                                onSeekFinish = {
                                    homeViewmodel.seekTo(it)
                                }
                            ) { playerOperation->
                                homeViewmodel.performOperation(playerOperation)
                            }
                        }

                    }

                }
        }
    }
    }
}


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesturefyPlayer(
    albumArt:Bitmap?,
    trackName:String,
    artistName:String,
    trackLength:Long,
    playbackPosition:Int,
    isShuffleOn: Boolean,
    isRepeatOn:Int,
    isPaused:Boolean = false,
    playerRestrictions: PlayerRestrictions,
    onSeek:(Int)->Unit,
    onSeekFinish:(Long)->Unit,
    onPlayerOperation: (PlayerOperation)->Unit
){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {

        Spacer(modifier =  Modifier.height(20.dp))

        Image(
                painter = painterResource(id = R.drawable.extended_spotify_logo),
                contentDescription = "Extended spotify logo",
                modifier = Modifier.height(50.dp)
            )

        Spacer(modifier = Modifier.height(20.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
            ) {
                    if(albumArt == null){
                    Image(
                        painter = painterResource(id = R.drawable.test_album_art),
                        contentDescription = "album art",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(350.dp)
                            .padding(start = 4.dp, end = 4.dp)
                    )}
                else{
                    androidx.compose.foundation.Image(
                        bitmap = albumArt.asImageBitmap(),
                        contentDescription = "Album art",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(350.dp)
                            .padding(start = 4.dp, end = 4.dp)
                    )
                    }
                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = artistName,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp)

                    )
                    Text(
                        text = trackName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp)
                    )
                Spacer(modifier = Modifier.height(12.dp))
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Slider(
                        value = (playbackPosition).toFloat(),
                        valueRange = 0f..(trackLength / 1000).toFloat(),
                        enabled = playerRestrictions.canSeek,
                        onValueChange = {
                            onSeek(it.toInt())
                        },
                        onValueChangeFinished = {
                            onSeekFinish(playbackPosition.toLong() * 1000)
                        },
                        track = {
                            SliderDefaults.Track(
                                sliderPositions = it,
                                modifier = Modifier.height(1.dp),
                                colors = SliderDefaults.colors(
                                    inactiveTickColor = Color.LightGray,
                                    activeTrackColor = Color.White,
                                )
                            )
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.LightGray,
                            disabledThumbColor = Color.DarkGray,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, start = 4.dp, end = 4.dp)
                ) {
                    val playbackPositionMin = playbackPosition/60
                    val playbackPositionSec = playbackPosition%60
                    val trackDuration = trackLength/1000
                    val trackDurationMin = trackDuration/60
                    val trackDurationSec = trackDuration%60
                    Text(
                        text = String.format("%2d:%02d",playbackPositionMin,playbackPositionSec),
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format("%2d:%02d",trackDurationMin,trackDurationSec),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        if(isShuffleOn) onPlayerOperation(PlayerOperation.SHUFFLE_OFF) else onPlayerOperation(PlayerOperation.SHUFFLE_ON)
                    }, enabled = playerRestrictions.canToggleShuffle,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContentColor = Color.DarkGray,
                            contentColor = if(isShuffleOn) SpotifyGreen else Color.White),
                        modifier = Modifier.size(25.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_shuffle_24),
                            contentDescription = "Shuffle",
                            modifier = Modifier.size(25.dp),
                        )
                    }
                    IconButton(
                        onClick = { onPlayerOperation(PlayerOperation.PREV) },
                        enabled = playerRestrictions.canSkipPrev,
                        colors = IconButtonDefaults.iconButtonColors(disabledContentColor = Color.DarkGray, contentColor = Color.White),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                            contentDescription = "Skip previous",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = {
                        if(isPaused) onPlayerOperation(PlayerOperation.PLAY) else onPlayerOperation(PlayerOperation.PAUSE)
                    },
                        modifier = Modifier.size(50.dp)) {
                        Icon(
                            painter = painterResource(id = if(isPaused) R.drawable.baseline_play_circle_filled_24 else R.drawable.baseline_pause_circle_24),
                            contentDescription = "play pause",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    IconButton(onClick = { onPlayerOperation(PlayerOperation.NEXT) },
                        enabled = playerRestrictions.canSkipNext,
                        colors = IconButtonDefaults.iconButtonColors(disabledContentColor = Color.DarkGray, contentColor = Color.White),
                        modifier = Modifier.size(40.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_skip_next_24),
                            contentDescription = "Skip Next",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(onClick = {
                        when(isRepeatOn){
                            Repeat.OFF -> onPlayerOperation(PlayerOperation.REPEAT_ONE)
                            Repeat.ALL -> onPlayerOperation(PlayerOperation.REPEAT_OFF)
                            Repeat.ONE -> onPlayerOperation(PlayerOperation.REPEAT_ALL)
                        }
                    },
                        enabled = playerRestrictions.canRepeatTrack,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContentColor = Color.DarkGray,
                            contentColor = if(isRepeatOn == Repeat.ONE || isRepeatOn == Repeat.ALL) SpotifyGreen else Color.White),
                        modifier = Modifier.size(25.dp)) {
                        Icon(
                            painter = painterResource(id = if(isRepeatOn == Repeat.OFF || isRepeatOn == Repeat.ALL) R.drawable.baseline_repeat_24 else R.drawable.baseline_repeat_one_24),
                            contentDescription = "repeat",
                            modifier = Modifier.size(25.dp),
                        )
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))
            }



    }

}

