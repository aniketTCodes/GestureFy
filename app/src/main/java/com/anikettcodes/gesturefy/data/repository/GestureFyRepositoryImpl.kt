package com.anikettcodes.gesturefy.data.repository

import com.anikettcodes.gesturefy.data.datasource.local.LocalDatasource
import com.anikettcodes.gesturefy.datastore.AuthorizationPreference
import com.anikettcodes.gesturefy.domain.repository.GestureFyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GestureFyRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDatasource
) : GestureFyRepository {

}