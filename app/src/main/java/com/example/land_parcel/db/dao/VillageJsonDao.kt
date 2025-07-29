package com.example.land_parcel.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.model.villageGeoJson.VillageGeoJson

@Dao
interface VillageJsonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVillageGeoJson(villageGeoJson: VillageGeoJson)

    @Query("SELECT * FROM VillageGeoJson WHERE VillageId = :villageId")
     suspend fun getVillageGeoJsonById(villageId: String): VillageGeoJson?

    @Query("UPDATE VillageGeoJson SET villageJson = :villageJson WHERE VillageId = :villageId")
    suspend fun updateVillageJson(villageId: String, villageJson: String?)
    @Query("UPDATE VillageGeoJson SET bBoxJson = :bBoxJson WHERE VillageId = :villageId")
    suspend fun updateBBOXJson(villageId: String, bBoxJson: String?)

    @Query("UPDATE VillageGeoJson SET changesJson = :changesJson WHERE VillageId = :villageId")
    suspend fun updateChangesJson(villageId: String, changesJson: String?)


}