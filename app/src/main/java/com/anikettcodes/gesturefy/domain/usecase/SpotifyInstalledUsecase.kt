package com.anikettcodes.gesturefy.domain.usecase

import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.util.Resource
import javax.inject.Inject

class SpotifyInstalledUsecase @Inject constructor(
    private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository
) {
    operator fun invoke():Resource<Boolean>{
        return try {
            Resource.Success(data = spotifyAppRemoteRepository.isSpotifyInstalled())
        } catch (e:Exception){
            Resource.Error(message = "Error while checking if spotify is installed")
        }
    }
}