package com.anikettcodes.gesturefy.presentation.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.anikettcodes.gesturefy.presentation.viewmodel.HomeViewmodel
import com.anikettcodes.gesturefy.R
import com.anikettcodes.gesturefy.presentation.ui.theme.GestureFyTheme
import com.anikettcodes.gesturefy.presentation.viewmodel.HomeState
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
    ) {
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

                        if(state.playerState != null){
                            var playBackPosition by rememberSaveable {
                                mutableIntStateOf(0)
                            }
                            LaunchedEffect(
                                key1 = state.playerState.isPaused,
                                key2 = state.playerState.playbackPosition
                            ) {
                                playBackPosition = (state.playerState.playbackPosition / 1000).toInt()
                                while (!state.playerState.isPaused) {
                                    Log.d("HOME_SCREEN", playBackPosition.toString())
                                    delay(1000)
                                    playBackPosition++
                                }
                            }
                            GesturefyPlayer(
                                trackName = state.playerState.track.name,
                                artistName = state.playerState.track.artist.name,
                                trackLength = state.playerState.track.duration,
                                playbackPosition = playBackPosition
                            )
                        }

                    }

                }
        }
    }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GesturefyPlayer(
    trackName:String,
    artistName:String,
    trackLength:Long,
    playbackPosition:Int
){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Image(
                painter = painterResource(id = R.drawable.extended_spotify_logo),
                contentDescription = "Extended spotify logo",
                modifier = Modifier.height(50.dp)
            )


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {

                    Image(
                        painter = painterResource(id = R.drawable.test_album_art),
                        contentDescription = "album art",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .size(350.dp)
                            .padding(start = 4.dp, end = 4.dp)
                    )
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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp)
                    )
                Slider(
                    value = (playbackPosition).toFloat(),
                    valueRange = 0f..(trackLength / 1000).toFloat(),
                    onValueChange = {},
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
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

            }



    }

}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun GestureFyPlayerPreview(){
    GestureFyTheme {
        GesturefyPlayer(trackName = "Wishes", artistName = "Billie Eilish", trackLength = 4500, playbackPosition =  200)
    }
}