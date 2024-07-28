package com.anikettcodes.gesturefy.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.anikettcodes.gesturefy.data.datasource.local.AuthorizationPreferenceSerializer
import com.anikettcodes.gesturefy.data.datasource.local.LocalDatasource
import com.anikettcodes.gesturefy.data.datasource.remote.SpotifyApi
import com.anikettcodes.gesturefy.data.repository.GestureFyRepositoryImpl
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.anikettcodes.gesturefy.domain.repository.GestureFyRepository
import com.anikettcodes.gesturefy.domain.usecase.GetAuthorizationDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun authorizationDataStoreProvider(@ApplicationContext context: Context):DataStore<AuthorizationPreference>{
        return DataStoreFactory.create(
            serializer = AuthorizationPreferenceSerializer,
            produceFile = {
                context.dataStoreFile("authorization_preference.pb")
            }
        )
    }

    @Provides
    @Singleton
    fun dataSourceProvider(dataStore:DataStore<AuthorizationPreference>):LocalDatasource = LocalDatasource(dataStore)

    @Provides
    @Singleton
    fun spotifyApiProvider():SpotifyApi = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpotifyApi::class.java)

    @Provides
    @Singleton
    fun gestureFyRepositoryProvider(datasource: LocalDatasource,remoteDataSource:SpotifyApi):GestureFyRepository = GestureFyRepositoryImpl(
        datasource,remoteDataSource
    )

    @Provides
    @Singleton
    fun authorizationDataUseCaseProvider(repository:GestureFyRepository):GetAuthorizationDataUseCase = GetAuthorizationDataUseCase(repository)

}