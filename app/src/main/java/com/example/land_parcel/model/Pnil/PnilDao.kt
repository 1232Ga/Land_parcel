package com.example.land_parcel.model.Pnil

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PnilDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPnil(pnilEntity: PnilEntity)

    @Query("SELECT * FROM pnil_table LIMIT 1")
    fun getPnil(): Flow<PnilEntity?>
}

