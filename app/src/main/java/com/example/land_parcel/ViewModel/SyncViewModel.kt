package com.example.land_parcel.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.land_parcel.PDFReport.ReportUpdate.Model.ReportRequest
import com.example.land_parcel.PDFReport.ReportUpdate.Response.ReportUpdateResponse
import com.example.land_parcel.model.ChangePassword.ChangePasswordRequest
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.repositories.SyncRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.Response
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(private val repository: SyncRepository) : ViewModel() {

    val surveyData = repository.surveyData
    val UpdateReportResponse=repository.updateReportState

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

    suspend fun getReportUpdate(token:String?,khasra_number:String,village_id:String?,status:Int,date:String,
                                owner:String,father_name:String,village_name:String,
                                district_name:String,tehsil:String,govt_id:String,
                                land_type:String,area:String,mobile_no:String,pnil_no:String?,
                                house_no:String,block:String,remark:String){
        val reportRequest = ReportRequest(khasra_number,village_id,status,date,owner,father_name,village_name,district_name,tehsil,govt_id,land_type,area,mobile_no,pnil_no,house_no,block,remark)
        val gson = Gson()
        val json = gson.toJson(reportRequest)
        println(json)
        repository.UpdateReport(token,reportRequest)
    }
}