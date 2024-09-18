package com.anikettcodes.gesturefy.data.entities

import com.google.gson.annotations.SerializedName

data class TokenEntity(
    @SerializedName("access_token")
    val accessToken:String,
    @SerializedName("refresh_token")
    val refreshToken:String,
    @SerializedName("expires_in")
    val expiresIn:Int
)