package com.example.land_parcel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.repositories.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(private val repository: SyncRepository) : ViewModel() {

    val surveyData = repository.surveyData

    init {
        viewModelScope.launch {
            getSurveyData()
        }
    }

    suspend fun getSurveyData() {
        repository.getSurveyData()
    }

    suspend fun getSurveyDataByPlotId(plotId: String): SurveyData? {
        return repository.getSurveyById(plotId)
    }

    suspend fun deleteSurveyDataByPlotId(plotId: String) {
        return repository.deleteSurveyById(plotId)
    }
}