package com.example.land_parcel.Interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.land_parcel.model.VillageModel.Village

@Dao
interface VillageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVillages(villages: List<Village>)

    @Query("SELECT * FROM villages")
    suspend fun getAllVillages(): List<Village>
}
