package com.anikettcodes.gesturefy.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(trackName: String, artistName: String,onNext:()->Unit,onPrev:()->Unit) {
   Column {
       Text(text = trackName)
       Text(text = artistName)
       Button(onClick = onNext) {
           Text(text = "Next")
       }

       Button(onClick = onPrev) {
           Text(text = "Prev")
       }
   } 
}