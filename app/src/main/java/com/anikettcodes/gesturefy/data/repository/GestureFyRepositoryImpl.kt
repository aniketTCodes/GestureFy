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


     override suspend fun isAuthorized(): Flow<Boolean> = localDatasource.getAuthorizationData().map {
         it.accessToken.isNotBlank()
     }

      @RequiresApi(Build.VERSION_CODES.O)
      override suspend fun authorize(data: Uri) {

          try{

              val authorizationCode:String? = data.getQueryParameter("code")
              val error:String? = data.getQueryParameter("error")
              val authHeaderString = "Basic ${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"

              if(error != null){
                  throw Exception(error)
              }

              if(authorizationCode == null){
                  throw Exception("Something went wrong")
              }

              val requestAccessTokenResult:AccessTokenModel = remoteDatasource.requestAccessToken(
                  code = authorizationCode,
                  redirectUri = BuildConfig.REDIRECT_URI,
                  grantType = "Authorization",
                  authHeader = Base64.getEncoder().encodeToString(authHeaderString.toByteArray())
              )

              localDatasource.storeAuthorizationCode(
                  requestAccessTokenResult.accessToken,
                  requestAccessTokenResult.expiresIn,
                  requestAccessTokenResult.refreshToken
              )

          } catch (e: Exception){
              Log.e(TAG,e.message?:"Unknown Error")
          }

      }

      companion object {

          const val TAG = "GestureFy Repository"

      }


  }