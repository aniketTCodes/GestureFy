package com.anikettcodes.gesturefy.domain.usecase

import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ConnectSpotifyAppRemoteUsecase @Inject constructor(
    private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository
){
   suspend operator fun invoke():Flow<Resource<Unit>> = flow {
       emit(Resource.Loading())
       try {
           spotifyAppRemoteRepository.connectSpotifyAppRemote()
           emit(Resource.Success(Unit))
       } catch (e:Exception){
           emit(Resource.Error(e.message?:"Could not connect to spotify"))
       }
   }
}