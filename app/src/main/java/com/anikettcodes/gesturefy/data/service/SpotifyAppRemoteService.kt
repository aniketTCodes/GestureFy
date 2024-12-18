package com.anikettcodes.gesturefy.data.service

import android.content.Context
import android.util.Log
import androidx.core.content.contentValuesOf
import com.anikettcodes.gesturefy.BuildConfig
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

class SpotifyAppRemoteService @Inject constructor(
    private val context: Context
) {
     var spotifyAppRemote:SpotifyAppRemote? = null
         private set

    fun isSpotifyInstalled() = SpotifyAppRemote.isSpotifyInstalled(context)

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun connect(){
        val connectionParams = ConnectionParams.Builder(BuildConfig.CLIENT_ID)
            .setRedirectUri(BuildConfig.REDIRECT_URI)
            .build()

        try{
            spotifyAppRemote = suspendCancellableCoroutine {
                SpotifyAppRemote.connect(context, connectionParams, object:Connector.ConnectionListener {
                    override fun onConnected(p0: SpotifyAppRemote?) {
                        Log.d(TAG,"Spotify remote connected")
                        it.resume(p0){}
                    }

                    override fun onFailure(p0: Throwable?) {
                        Log.d(TAG,p0?.message?:"Unknown")
                        spotifyAppRemote = null
                        if(!it.isCompleted) it.resumeWithException(p0!!)
                        else Log.e(TAG,"Remote connection failed after coroutinecancellation")
                    }

                })
            }
        } catch (e:Exception){
            Log.e(TAG,e.message?:"Failed to connect to spotify app remote")
            throw e
        }
    }
    companion object {
        const val TAG = "SPOTIFY_APP_REMOTE_SERVICE"
    }
}