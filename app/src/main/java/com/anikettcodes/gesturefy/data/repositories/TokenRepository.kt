package com.anikettcodes.gesturefy.data.repositories

import android.content.SharedPreferences
import com.anikettcodes.gesturefy.data.datasource.remote.SpotifyAuthorizationApi
import javax.inject.Inject

class TokenRepository @Inject constructor(
    val sharedPrefs:SharedPreferences,
    val spotifyAuthorizationApi: SpotifyAuthorizationApi
) {

    fun getAccessToken

}