package com.anikettcodes.gesturefy.presentation.viewmodels

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anikettcodes.gesturefy.domain.usecases.AuthorizationUseCase
import com.anikettcodes.gesturefy.domain.usecases.LoginUseCase
import com.anikettcodes.gesturefy.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewmodel @Inject constructor(
    private val authorizationUseCase: AuthorizationUseCase,
    private val loginUseCase: LoginUseCase
):ViewModel() {

    private val _state = mutableStateOf<AuthorizationState>(
        AuthorizationState(
            isLoading = false,
            isLoggedIn = false,
            errorMessage = null
        )
    )
    val state = _state

    init {
        _state.value = _state.value.copy(isLoggedIn = authorizationUseCase())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun authorizeUser(data: Uri){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                loginUseCase(data).collect{

                    when(it){
                        is Resource.Success -> {
                            _state.value = _state.value.copy(isLoggedIn = authorizationUseCase(), isLoading = false)
                        }

                        is Resource.Error -> {
                            _state.value = _state.value.copy(isLoading = false, errorMessage = it.message)
                        }
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(isLoading = true)
                        }
                    }

                }
            }
        }
    }

}

data class AuthorizationState(
    val isLoading:Boolean,
    val isLoggedIn:Boolean,
    val errorMessage:String?
)