package com.anikettcodes.gesturefy.domain.repository

import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import kotlinx.coroutines.flow.Flow

interface GestureFyRepository {

    suspend fun getAuthorizationData():Flow<Boolean>

}