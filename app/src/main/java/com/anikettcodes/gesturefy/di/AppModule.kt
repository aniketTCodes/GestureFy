package com.anikettcodes.gesturefy.di

import android.content.Context
import android.content.SharedPreferences
import com.anikettcodes.gesturefy.data.datasource.remote.SpotifyAuthorizationApi
import com.anikettcodes.gesturefy.data.repositories.GesturefyRepository
import com.anikettcodes.gesturefy.data.repositories.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.data.repositories.TokenRepository
import com.anikettcodes.gesturefy.data.service.SpotifyAppRemoteService
import com.anikettcodes.gesturefy.domain.usecases.AuthorizationUseCase
import com.anikettcodes.gesturefy.domain.usecases.LoginUseCase
import com.anikettcodes.gesturefy.domain.usecases.PlayerStateUseCase
import com.anikettcodes.gesturefy.utils.PREFS_TOKEN_FILE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun playerStateUseCaseProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository):PlayerStateUseCase =
        PlayerStateUseCase(spotifyAppRemoteRepository)

    @Provides
    @Singleton
    fun spotifyAppRemoteServiceProvider(@ApplicationContext context: Context):SpotifyAppRemoteService =
        SpotifyAppRemoteService(context)

    @Provides
    @Singleton
    fun spotifyAppRemoteRepositoryProvider(spotifyAppRemoteService: SpotifyAppRemoteService):SpotifyAppRemoteRepository=
        SpotifyAppRemoteRepository(spotifyAppRemoteService)

    @Provides
    @Singleton
    fun sharedPreferenceProvider(@ApplicationContext context: Context):SharedPreferences{
        return context.getSharedPreferences(PREFS_TOKEN_FILE,Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun okHttpClientProvider():OkHttpClient{
        val interceptor = HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
        return client
    }

    @Provides
    @Singleton
    fun spotifyAuthorizationApiProvider(client: OkHttpClient):SpotifyAuthorizationApi = Retrofit.Builder()
        .baseUrl("https://accounts.spotify.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(SpotifyAuthorizationApi::class.java)

    @Provides
    @Singleton
    fun tokenRepositoryProvider(sharedPreferences: SharedPreferences,spotifyAuthorizationApi: SpotifyAuthorizationApi):TokenRepository = TokenRepository(sharedPreferences,spotifyAuthorizationApi)

    @Provides
    @Singleton
    fun gesturefyRepositoryProvider(tokenRepository: TokenRepository):GesturefyRepository = GesturefyRepository(tokenRepository)

    @Provides
    @Singleton
    fun loginUseCaseProvider(gesturefyRepository: GesturefyRepository):LoginUseCase = LoginUseCase(gesturefyRepository)

    @Provides
    @Singleton
    fun authorizationUseCaseProvider(gesturefyRepository: GesturefyRepository):AuthorizationUseCase = AuthorizationUseCase(gesturefyRepository)
}