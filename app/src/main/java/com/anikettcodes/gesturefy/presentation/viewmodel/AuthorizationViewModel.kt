package com.anikettcodes.gesturefy.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anikettcodes.gesturefy.domain.usecase.GetAuthorizationDataUseCase
import com.anikettcodes.gesturefy.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val authorizationDataUseCase: GetAuthorizationDataUseCase
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
        viewModelScope.launch {
            authorizationDataUseCase().collect{
                when(it){
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, errorMessage = it.message)
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, isLoggedIn = it.data!!)
                    }
                }
            }
        }
    }

    fun authorizeUsingCode(data: Uri){

    }

}

data class AuthorizationState(
    val isLoading:Boolean,
    val isLoggedIn:Boolean,
    val errorMessage:String?
)