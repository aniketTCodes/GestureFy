package com.anikettcodes.gesturefy.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.anikettcodes.gesturefy.data.datasource.local.AuthorizationPreferenceSerializer
import com.anikettcodes.gesturefy.data.datasource.local.LocalDatasource
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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

}