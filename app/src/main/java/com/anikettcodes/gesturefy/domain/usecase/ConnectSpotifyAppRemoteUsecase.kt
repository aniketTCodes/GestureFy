package com.anikettcodes.gesturefy.domain.usecase

import com.anikettcodes.gesturefy.data.repository.SpotifyAppRemoteRepository
import com.anikettcodes.gesturefy.util.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
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
           if(e.message == null) emit(Resource.Error("Failed to connect to spotify app remote"))
           else{
               try {
                   val errorJson = JSONObject(e.message!!)
                   emit(Resource.Error(errorJson.getString("message")))
               } catch (jsonException:Exception){
                   emit(Resource.Error(e.message!!))
               }
           }
       }
   }
}