package com.anikettcodes.gesturefy.domain.usecase

import androidx.lifecycle.LifecycleOwner
import com.anikettcodes.gesturefy.data.repository.GestureFyRepository
import com.anikettcodes.gesturefy.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GestureRecognizerUsecase @Inject constructor(
    private val gestureFyRepository: GestureFyRepository
) {

      suspend operator fun invoke(lifecycleOwner: LifecycleOwner):Resource<Unit>{
          try{
                gestureFyRepository.startGestureRecognition(lifecycleOwner)
              return Resource.Success(Unit)
          }catch (e:Exception){
              return Resource.Error(e.message?:"Unknown Error")
          }
      }



}