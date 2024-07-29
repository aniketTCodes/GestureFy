package com.anikettcodes.gesturefy.domain.usecase

import android.net.Uri
import com.anikettcodes.gesturefy.domain.repository.GestureFyRepository
import com.anikettcodes.gesturefy.util.Resource
import com.google.protobuf.BoolValueKt
import javax.inject.Inject

class AuthorizeSpotifyUseCase @Inject constructor(
    private val repository: GestureFyRepository
) {

     suspend operator fun invoke(data: Uri):Resource<Unit>{
         try{

             repository.authorize(data)
             return Resource.Success(data = Unit)
         } catch (e: Exception){
             return Resource.Error(e.message?:"Unknown")
         }
     }

}