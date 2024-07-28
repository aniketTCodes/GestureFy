package com.anikettcodes.gesturefy.domain.repository

import android.net.Uri
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import kotlinx.coroutines.flow.Flow

interface GestureFyRepository {

    suspend fun isAuthorized():Flow<Boolean>

    suspend fun authorize(data: Uri)

}