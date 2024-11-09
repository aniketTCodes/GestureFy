package com.anikettcodes.gesturefy.domain.usecase

import androidx.datastore.dataStore
import com.anikettcodes.gesturefy.data.repository.PlayerOperation
import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.util.Resource
import okhttp3.Response
import javax.inject.Inject

class PlaybackControlUsecase @Inject constructor(
     private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository
) {

    operator fun invoke(playerOperation: PlayerOperation):Resource<Unit>{
        try {
            spotifyAppRemoteRepository.performPlayerOperation(playerOperation)
            return Resource.Success(data = Unit)
        } catch (e:Exception){
            return Resource.Error(message = e.message?:"Could not perform operation")
        }
    }

}