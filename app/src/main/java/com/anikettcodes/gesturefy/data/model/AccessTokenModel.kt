package com.anikettcodes.gesturefy.data.model

data class AccessTokenModel(
    val accessToken:String,
    val expiresIn:Int,
    val refreshToken:String
)
