package com.anikettcodes.gesturefy.data.datasource.local

import android.util.Log
import androidx.datastore.core.DataStore
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject


/*
Stores Authorization Data locally
 */

class LocalDatasource @Inject constructor (
    private val protoDataStore: DataStore<AuthorizationPreference>
) {

    //Store authorization details locally
    suspend fun storeAuthorizationCode(
        accessToken: String,
        expiresIn: Int,
        refreshToken: String
    ){
        protoDataStore.updateData {prefs ->
            prefs.toBuilder()
                .setAccessToken(accessToken)
                .setExpiresIn(expiresIn)
                .setRefreshToken(refreshToken)
                .build()
        }
    }

    //Delete stored authorization tokens in case of log out
    suspend fun clearAuthorizationData(
    ){
        protoDataStore.updateData {prefs ->
            prefs.toBuilder()
                .clear()
                .build()
        }
    }

    //Read authorization token to fetch user data
    suspend fun getAuthorizationData():Flow<AuthorizationPreference> = protoDataStore.data
        .catch {exception ->
            if(exception is IOException){
                Log.e(TAG,"IOException while reading Authorization token")
                emit(AuthorizationPreference.getDefaultInstance())
            }
            else{
                throw exception
            }
        }

    companion object {
        const val TAG = "Local Datastore"
    }

}