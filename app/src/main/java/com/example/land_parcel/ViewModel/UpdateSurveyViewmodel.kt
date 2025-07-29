package com.example.land_parcel.viewmodel

import androidx.lifecycle.ViewModel
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.repositories.UpdateFormRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateSurveyViewmodel @Inject constructor(private val repository: UpdateFormRepository):ViewModel() {


    suspend fun updateSurveyData(surveyData: SurveyData){
        repository.updateForm(surveyData)
    }
}