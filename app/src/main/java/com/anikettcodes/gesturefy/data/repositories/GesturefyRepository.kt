package com.anikettcodes.gesturefy.data.repositories

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import java.net.URI
import javax.inject.Inject

class GesturefyRepository @Inject constructor(
    private val tokenRepository: TokenRepository
) {

     @RequiresApi(Build.VERSION_CODES.O)
     suspend fun login(data:Uri){

         tokenRepository.getTokenFromCode(data)

     }

    fun isLoggedIn() = tokenRepository.isTokenSaved()



}