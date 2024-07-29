package com.anikettcodes.gesturefy.data.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class AccessTokenModel(
    @SerializedName("access_token")
    val accessToken:String,
    @SerializedName("expires_in")
    val expiresIn:Int,
    @SerializedName("refresh_token")
    val refreshToken:String
)
