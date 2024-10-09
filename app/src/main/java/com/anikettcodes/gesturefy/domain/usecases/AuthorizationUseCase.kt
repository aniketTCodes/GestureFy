package com.anikettcodes.gesturefy.domain.usecases

import com.anikettcodes.gesturefy.data.repositories.GesturefyRepository
import javax.inject.Inject

class AuthorizationUseCase @Inject constructor(
    private val gesturefyRepository: GesturefyRepository
) {

    operator fun invoke():Boolean = gesturefyRepository.isLoggedIn()

}