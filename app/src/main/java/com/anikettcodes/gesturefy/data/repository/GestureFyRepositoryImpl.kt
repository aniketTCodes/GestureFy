package com.anikettcodes.gesturefy.data.repository

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.anikettcodes.gesturefy.BuildConfig
import com.anikettcodes.gesturefy.data.datasource.local.LocalDatasource
import com.anikettcodes.gesturefy.data.datasource.remote.SpotifyApi
import com.anikettcodes.gesturefy.data.model.AccessTokenModel
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.anikettcodes.gesturefy.domain.repository.GestureFyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Base64
import javax.inject.Inject

  class GestureFyRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDatasource,
    private val remoteDatasource: SpotifyApi
) : GestureFyRepository {


    private var accessToken: String = ""
    private var refreshToken: String = ""


     override suspend fun isAuthorized(): Flow<Boolean> = localDatasource.getAuthorizationData().map {
         setTokens(it.accessToken,it.refreshToken)
         it.accessToken.isNotBlank()
     }

      @RequiresApi(Build.VERSION_CODES.O)
      override suspend fun authorize(data: Uri):Boolean{
          Log.e(TAG,"Authorize Function called")
          try{

              val authorizationCode:String? = data.getQueryParameter("code")
              val error:String? = data.getQueryParameter("error")
              val authHeaderString = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
              val encodedAuthHeader = Base64.getEncoder().encodeToString(authHeaderString.toByteArray())

              if(error != null){
                  throw Exception(error)

              }

              if(authorizationCode == null){
                  throw Exception("Something went wrong")
              }

              val requestAccessTokenResult:AccessTokenModel = remoteDatasource.requestAccessToken(
                  code = authorizationCode,
                  redirectUri = BuildConfig.REDIRECT_URI,
                  grantType = "authorization_code",
                  authHeader = "Basic $encodedAuthHeader"
              )

              localDatasource.storeAuthorizationCode(
                  requestAccessTokenResult.accessToken,
                  requestAccessTokenResult.expiresIn,
                  requestAccessTokenResult.refreshToken
              )

              return true;

          } catch (e: Exception){
              Log.e(TAG,e.message?:"Unknown Error")
              throw e
          }
      }


       override suspend fun refreshToken(){
          try{
              val authHeader =
          }
      }

      private fun setTokens(newAccessToken: String, newRefreshToken: String){
          if(newAccessToken != accessToken || newRefreshToken != refreshToken){
              accessToken = newAccessToken
              refreshToken = newRefreshToken
              Log.d(TAG, "Tokens updated")
          }
      }

      public fun getAccessToken():String{
          return accessToken
      }

      companion object {

          const val TAG = "GestureFy_Repository"

      }


  }