package com.anikettcodes.gesturefy.data.repositories

import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.data.datasource.remote.SpotifyAuthorizationApi
import com.anikettcodes.gesturefy.data.entities.TokenEntity
import com.anikettcodes.gesturefy.utils.ACCESS_TOKEN_KEY
import com.anikettcodes.gesturefy.utils.REFRESH_TOKEN_KEY
import com.anikettcodes.gesturefy.utils.VALID_TILL_KEY
import java.time.Instant
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val sharedPrefs:SharedPreferences,
    private val spotifyAuthorizationApi: SpotifyAuthorizationApi
) {

    fun isTokenSaved():Boolean = sharedPrefs.getString(ACCESS_TOKEN_KEY,null) != null

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAccessToken():String{

        val accessToken = sharedPrefs.getString(ACCESS_TOKEN_KEY,"")
        val validTill = Instant.parse(sharedPrefs.getString(VALID_TILL_KEY,""))

        if(Instant.now().isBefore(validTill.minusSeconds(1))) return accessToken?:""

        return refreshToken()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTokens(tokenEntity: TokenEntity){

        sharedPrefs.edit {
            putString(ACCESS_TOKEN_KEY,tokenEntity.accessToken)
            putString(REFRESH_TOKEN_KEY,tokenEntity.refreshToken)
            putString(VALID_TILL_KEY, Instant.now().plusSeconds(tokenEntity.expiresIn.toLong()).toString())
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
     suspend fun getTokenFromCode(data: Uri){
        try{
            val authorizationCode:String? = data.getQueryParameter("code")
            val error:String? = data.getQueryParameter("error")
            val authHeaderString = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
            val encodedAuthHeader = java.util.Base64.getEncoder().encodeToString(authHeaderString.toByteArray())

            if(error != null) throw Exception(error)

            if(authorizationCode == null) throw Exception("Authorization failed")

            val requestAccessTokenResult: TokenEntity = spotifyAuthorizationApi.requestAccessToken(
                code = authorizationCode,
                redirectUri = BuildConfig.REDIRECT_URI,
                grantType = "authorization_code",
                authHeader = "Basic $encodedAuthHeader"
            )

            saveTokens(requestAccessTokenResult)
        } catch (e:Exception){
            Log.e(TAG, e.message?:"Unknown")
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun refreshToken():String{

        val authHeaderString = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
        val encodedAuthHeader = java.util.Base64.getEncoder().encodeToString(authHeaderString.toByteArray())
        val refreshToken = sharedPrefs.getString(REFRESH_TOKEN_KEY,null)

        assert(refreshToken != null)

        try {
            val refreshTokenResult = spotifyAuthorizationApi.requestRefreshToken(
                grantType = "refresh_token",
                refreshToken = refreshToken!!,
                clientId = BuildConfig.CLIENT_ID,
                authHeader = encodedAuthHeader
            )

            saveTokens(refreshTokenResult)
            return refreshTokenResult.accessToken
        } catch (e: Exception) {
            Log.e(TAG,e.message?:"UNKNOWN")
            throw e
        }

    }

    companion object {
       const val TAG = "TOKEN REPOSITORY"
    }

}