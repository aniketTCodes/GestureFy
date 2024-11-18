package com.anikettcodes.gesturefy.domain.usecase

import android.graphics.Bitmap
import android.media.Image
import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.util.Resource
import com.spotify.protocol.types.ImageUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlbumArtUseCase @Inject  constructor(
    private val spotifyAppRemoteRepository: SpotifyAppRemoteRepository
) {
    operator fun invoke(uri:ImageUri):Flow<Resource<Bitmap>> = flow {
        emit(Resource.Loading())
        try{
            emit(
                Resource.Success(
                    data = spotifyAppRemoteRepository.getTrackArt(uri)
                )
            )
        }catch (e:Exception){
            emit(Resource.Error(message = e.message?:"Error getting album art"))
        }
    }


}