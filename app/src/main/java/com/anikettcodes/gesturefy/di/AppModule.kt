package com.anikettcodes.gesturefy.di

import android.content.Context
import com.anikettcodes.gesturefy.data.repository.GestureFyRepository
import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.data.service.CameraService
import com.anikettcodes.gesturefy.data.service.GestureRecognizerService
import com.anikettcodes.gesturefy.data.service.SpotifyAppRemoteService
import com.anikettcodes.gesturefy.domain.usecase.AlbumArtUseCase
import com.anikettcodes.gesturefy.domain.usecase.ConnectSpotifyAppRemoteUsecase
import com.anikettcodes.gesturefy.domain.usecase.GestureRecognizerUsecase
import com.anikettcodes.gesturefy.domain.usecase.PlaybackControlUsecase
import com.anikettcodes.gesturefy.domain.usecase.PlayerStateUsecase
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

    @Provides
    @Singleton
    fun playerStateUsecaseProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository) =
        PlayerStateUsecase(spotifyAppRemoteRepository)

    @Provides
    @Singleton
    fun playbackControlUsecaseProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository) =
        PlaybackControlUsecase(spotifyAppRemoteRepository)

    @Provides
    @Singleton
    fun albumArtUseCaseProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository) = AlbumArtUseCase(spotifyAppRemoteRepository)

    @Provides
    @Singleton
    fun cameraServiceProvider(@ApplicationContext context: Context) = CameraService(context)

    @Provides
    @Singleton
    fun gestureRecognizerServiceProvider(@ApplicationContext context: Context) = GestureRecognizerService(context)

    @Provides
    @Singleton
    fun gestureFyRepositoryProvider(spotifyAppRemoteRepository: SpotifyAppRemoteRepository, gestureRecognizerService: GestureRecognizerService, cameraService: CameraService)
    = GestureFyRepository(spotifyAppRemoteRepository, gestureRecognizerService, cameraService)

    @Provides
    @Singleton
    fun gestureFyUsecaseProvider(gestureFyRepository: GestureFyRepository) = GestureRecognizerUsecase(gestureFyRepository)
}