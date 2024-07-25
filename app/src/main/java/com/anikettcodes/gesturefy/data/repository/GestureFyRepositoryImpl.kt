package com.anikettcodes.gesturefy.data.repository

import com.anikettcodes.gesturefy.data.datasource.local.LocalDatasource
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.anikettcodes.gesturefy.domain.repository.GestureFyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

 class GestureFyRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDatasource
) : GestureFyRepository {
     override suspend fun getAuthorizationData(): Flow<Boolean> = flow {

     }

 }