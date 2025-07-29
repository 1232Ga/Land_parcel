package com.example.land_parcel.repositories

import com.example.land_parcel.db.LandDatabase
import com.example.land_parcel.model.survey.SurveyData
import javax.inject.Inject

class UpdateFormRepository @Inject constructor(landDatabase: LandDatabase) {
    val surveyDataDao = landDatabase.getSurveyDataDao()

    suspend fun updateForm(surveyData: SurveyData) {
        surveyDataDao.addSurvey(surveyData)
    }
}