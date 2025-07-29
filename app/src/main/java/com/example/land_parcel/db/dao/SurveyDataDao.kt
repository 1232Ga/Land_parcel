package com.example.land_parcel.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.land_parcel.model.survey.SurveyData

@Dao
interface SurveyDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSurvey(surveyData: SurveyData)

    @Query("Select * from  SurveyData")
    suspend fun getSurvey(): List<SurveyData>

    @Query("SELECT * FROM SurveyData WHERE Khasra_No = :plotId")
    suspend fun getSurveyByPlotId(plotId: String): SurveyData?

    @Query("DELETE FROM SurveyData WHERE Khasra_No = :plotId")
    suspend fun deleteSurveyByPlotId(plotId: String)
}