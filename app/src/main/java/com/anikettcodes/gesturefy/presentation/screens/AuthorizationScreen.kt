package com.anikettcodes.gesturefy.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.anikettcodes.gesturefy.presentation.viewmodel.AuthorizationViewModel

@Composable
fun AuthorizationScreen(
    viewModel: AuthorizationViewModel,
    onAuthorize:()->Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onAuthorize) {
            Text(text = "Authorize Spotify")
        }
    }
}