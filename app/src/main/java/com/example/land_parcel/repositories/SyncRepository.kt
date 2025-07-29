package com.example.land_parcel.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.land_parcel.db.LandDatabase
import com.example.land_parcel.model.survey.SurveyData
import javax.inject.Inject

class SyncRepository @Inject constructor(landDatabase: LandDatabase) {
    private val surveyDataDao = landDatabase.getSurveyDataDao()
    private val _surveyData = MutableLiveData<List<SurveyData>>()
    val surveyData: LiveData<List<SurveyData>> get() = _surveyData

    suspend fun getSurveyData() {
        _surveyData.postValue(surveyDataDao.getSurvey())
    }
    suspend fun getSurveyById(plotId:String): SurveyData? {
      return  surveyDataDao.getSurveyByPlotId(plotId)
    }

    suspend fun deleteSurveyById(plotId:String) {
       surveyDataDao.deleteSurveyByPlotId(plotId)
    }
}