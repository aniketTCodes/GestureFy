package com.anikettcodes.gesturefy.presentation.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.anikettcodes.gesturefy.presentation.viewmodel.HomeState
import com.anikettcodes.gesturefy.presentation.viewmodel.HomeViewmodel

@Composable
fun HomeScreen(){
    val homeViewmodel = hiltViewModel<HomeViewmodel>()
    val state = homeViewmodel.state


}