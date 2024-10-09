package com.anikettcodes.gesturefy.domain.usecases

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.anikettcodes.gesturefy.data.repositories.GesturefyRepository
import com.anikettcodes.gesturefy.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URI
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository:GesturefyRepository
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(data:Uri):Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try{
            repository.login(data)
            emit(Resource.Success(data = true))
        }catch (e:Exception){
            emit(Resource.Error(message = "Authorization failed"))
        }
    }

}