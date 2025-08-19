package com.example.land_parcel.model.Pnil

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PnilRepository @Inject constructor(
    private val pnilDao: PnilDao
) {
    suspend fun savePnil(pnilNo: String?) {
        pnilDao.insertPnil(PnilEntity(pnilNo = pnilNo))
    }

    fun getPnil() = pnilDao.getPnil()
}

