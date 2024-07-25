package com.anikettcodes.gesturefy.presentation

sealed class Destinations(val route:String){

    data object AuthorizationScreen:Destinations(route = "/authorization")

    data object HomeScreen:Destinations(route = "/home")

}