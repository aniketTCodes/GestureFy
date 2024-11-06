package com.anikettcodes.gesturefy.data.repositories

import android.util.Log
import com.anikettcodes.gesturefy.data.service.SpotifyAppRemoteService
import com.anikettcodes.gesturefy.utils.Resource
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class SpotifyAppRemoteRepository @Inject constructor(
    private val spotifyAppRemoteService: SpotifyAppRemoteService
) {


    private suspend fun setupSpotifyAppRemote(){
        try{
            spotifyAppRemoteService.connect()

        } catch (e:Exception){
            Log.e(TAG,e.message?:"Unknown message")
            throw e
        }
    }

    suspend fun getPlayerState():Flow<PlayerState>{
        setupSpotifyAppRemote()
        return callbackFlow {
            val remote = spotifyAppRemoteService.spotifyAppRemote
            if(remote == null) close(IllegalStateException("Spotify not connected"))
            val subscription = remote!!.playerApi.subscribeToPlayerState()
            subscription
                .setEventCallback {
                trySend(it)
            }
                .setErrorCallback {
                    close(it)
                }
            awaitClose{subscription.cancel()}
        }
    }


   
    companion object{
        const val TAG = "SPOTIFY_APP_REMOTE_REPOSITORY"
    }
}