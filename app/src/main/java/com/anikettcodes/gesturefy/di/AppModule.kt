package com.anikettcodes.gesturefy.di

import android.content.Context
import android.content.SharedPreferences
import com.anikettcodes.gesturefy.data.datasource.remote.SpotifyAuthorizationApi
import com.anikettcodes.gesturefy.utils.PREFS_TOKEN_FILE
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun sharedPreferenceProvider(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences(PREFS_TOKEN_FILE,Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun spotifyAuthorizationApiProvider():SpotifyAuthorizationApi = Retrofit.Builder()
        .baseUrl("https://accounts.spotify.com/api/token")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyAuthorizationApi::class.java)

}