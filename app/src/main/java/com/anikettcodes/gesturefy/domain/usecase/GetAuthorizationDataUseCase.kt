package com.anikettcodes.gesturefy.domain.usecase

import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.anikettcodes.gesturefy.domain.repository.GestureFyRepository
import com.anikettcodes.gesturefy.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class GetAuthorizationDataUseCase @Inject constructor(
    private val repository: GestureFyRepository
) {

    suspend operator fun invoke(): Flow<Resource<AuthorizationPreference>> = flow {
        emit(Resource.Loading())
        try{
            repository.getAuthorizationData().collect{
                emit(Resource.Success(data = it))
            }
        } catch(e:Exception){
            emit(Resource.Error(message = "Error reading authorization token"))
        }
    }

}