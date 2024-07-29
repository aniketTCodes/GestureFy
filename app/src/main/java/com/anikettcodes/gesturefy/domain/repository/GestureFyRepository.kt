package com.anikettcodes.gesturefy.domain.repository

import android.net.Uri
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.anikettcodes.gesturefy.util.Resource
import kotlinx.coroutines.flow.Flow

interface GestureFyRepository {

    suspend fun isAuthorized():Flow<Boolean>

    suspend fun authorize(data: Uri):Boolean

}