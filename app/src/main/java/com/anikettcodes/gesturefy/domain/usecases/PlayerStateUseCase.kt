package com.anikettcodes.gesturefy.domain.usecases

import com.anikettcodes.gesturefy.data.repositories.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.utils.Resource
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerStateUseCase @Inject constructor(
    private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository
) {
    suspend operator fun invoke():Flow<Resource<PlayerState>> = flow {
        emit(Resource.Loading())

        try {
            spotifyAppRemoteRepository.getPlayerState().collect{
                emit(Resource.Success(data = it))
            }
        }catch (e:Exception){
            emit(Resource.Error(message = e.message?:"Unknown error"))
        }
    }

}