package com.anikettcodes.gesturefy.domain.usecase

import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.util.Resource
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Response
import javax.inject.Inject

class PlayerStateUsecase @Inject constructor(
    private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository
) {
    operator fun invoke(): Flow<Resource<PlayerState>> =  flow {
        try{
            spotifyAppRemoteRepository.getPlayerState().collect{
                emit(Resource.Success(data = it))
            }
        } catch (e:Exception){
            emit(Resource.Error(message = e.message?:"Error getting player state"))
        }
    }
}