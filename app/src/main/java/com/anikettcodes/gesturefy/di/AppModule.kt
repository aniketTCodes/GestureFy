package com.anikettcodes.gesturefy.di

import android.content.Context
import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.data.service.SpotifyAppRemoteService
import com.anikettcodes.gesturefy.domain.usecase.ConnectSpotifyAppRemoteUsecase
import com.anikettcodes.gesturefy.domain.usecase.SpotifyInstalledUsecase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun spotifyAppRemoteServiceProvider(@ApplicationContext context: Context) =
        SpotifyAppRemoteService(context)

    @Provides
    @Singleton
    fun spotifyAppRemoteRepositoryProvider(spotifyAppRemoteService: SpotifyAppRemoteService) =
        SpotifyAppRemoteRepository(spotifyAppRemoteService)

    @Provides
    @Singleton
    fun connectSpotifyAppRemoteUsecaseProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository) = ConnectSpotifyAppRemoteUsecase(spotifyAppRemoteRepository)

    @Provides
    @Singleton
    fun spotifyInstalledUsecaseProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository) = SpotifyInstalledUsecase(spotifyAppRemoteRepository)
}