package com.anikettcodes.gesturefy.data.repository

import android.util.Log
import com.anikettcodes.gesturefy.data.service.SpotifyAppRemoteService
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SpotifyAppRemoteRepository @Inject constructor(
    private val spotifyAppRemoteService: SpotifyAppRemoteService
) {

   fun isSpotifyInstalled():Boolean{
       try{
           return spotifyAppRemoteService.isSpotifyInstalled()
       } catch (e:Exception){
           Log.e(TAG,e.message?:"Unknown error while checking if spotify is installed")
           throw Exception("Error while checking if spotify is installed")
       }
   }

    suspend fun connectSpotifyAppRemote(){
        try{
            if(!isSpotifyInstalled()) throw Exception("Spotify is not installed")

            spotifyAppRemoteService.connect()

        } catch (e:Exception){
            Log.e(TAG,e.message?:"Unknown error while connecting to spotify app remote")
            throw e
        }
    }

    fun getPlayerState(): Flow<PlayerState> {
       val remote = spotifyAppRemoteService.spotifyAppRemote
           ?: throw IllegalStateException("Spotify app remote is not connected")

        return callbackFlow {
            val subscription = remote.playerApi.subscribeToPlayerState()
            subscription.setEventCallback {
                trySend(it)
            }
            subscription.setErrorCallback {
                Log.e(TAG,it.message?:"Unknown error while subscribing to player state")
                close(it)
            }
            awaitClose{subscription.cancel()}
        }
    }

    fun performPlayerOperation(operation: PlayerOperation){
        val remote = spotifyAppRemoteService.spotifyAppRemote
            ?: throw IllegalStateException("Spotify app remote is not connected")

        try {
            when(operation){
                PlayerOperation.PLAY -> remote.playerApi.resume()
                PlayerOperation.PAUSE -> remote.playerApi.pause()
                PlayerOperation.NEXT -> remote.playerApi.skipNext()
                PlayerOperation.PREV -> remote.playerApi.skipPrevious()
            }
        } catch (e:Exception){
            Log.e(TAG,e.message?:"Unknown error while performing player operation")
            throw e
        }
    }

    companion object{
        const val TAG = "SPOTIFY_APP_REMOTE_REPOSITORY"
    }
}

enum class PlayerOperation{
    PLAY,
    PAUSE,
    NEXT,
    PREV
}