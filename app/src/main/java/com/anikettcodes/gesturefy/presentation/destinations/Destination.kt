package com.anikettcodes.gesturefy.presentation.destinations

sealed class Destination(val route:String) {

    data object AuthorizationScreen:Destination(route = "/authorization")

    data object HomeScreen:Destination(route = "/home")
}