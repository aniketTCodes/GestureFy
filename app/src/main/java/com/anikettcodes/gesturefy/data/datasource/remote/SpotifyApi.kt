package com.anikettcodes.gesturefy.data.datasource.remote

import com.anikettcodes.gesturefy.data.model.AccessTokenModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SpotifyApi {


    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("https://accounts.spotify.com/api/token")
    suspend fun requestAccessToken(
        @Field("code") code:String,
        @Field("redirect_uri") redirectUri:String,
        @Field("grant_type") grantType:String,
        @Header("Authorization") authHeader:String
    ):AccessTokenModel

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("https://accounts.spotify.com/api/token")
    suspend fun requestRefreshToken(
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Header("Authorization") authHeader:String
    ):AccessTokenModel

}