package com.anikettcodes.gesturefy.data.entities

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class SavedTokenEntity(
    val accessToken:String,
    val refreshToken:String,
    val validTill:String
)
