package com.example.land_parcel.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.land_parcel.PDFReport.ReportUpdate.Model.ReportRequest
import com.example.land_parcel.PDFReport.ReportUpdate.Response.ReportUpdateResponse
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.Utils
import com.example.land_parcel.db.LandDatabase
import com.example.land_parcel.di.qualifiers.LandQualifiers
import com.example.land_parcel.model.ChangePassword.ChangePasswordRequest
import com.example.land_parcel.model.ChangePassword.ChangePasswordResponse
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.network.ApiService
import com.example.land_parcel.network.NetworkSealed
import okhttp3.Response
import javax.inject.Inject

class SyncRepository @Inject constructor(landDatabase: LandDatabase,
                                         @LandQualifiers.VisualizationRetrofit val apiService: ApiService,
                                         val networkUtils: NetworkUtils) {

    private val surveyDataDao = landDatabase.getSurveyDataDao()
    private val _surveyData = MutableLiveData<List<SurveyData>>()
    val surveyData: LiveData<List<SurveyData>> get() = _surveyData

    private val _updateReportState = MutableLiveData<NetworkSealed<ReportUpdateResponse>>()
    val updateReportState: LiveData<NetworkSealed<ReportUpdateResponse>> = _updateReportState



    suspend fun getSurveyData() {
        _surveyData.postValue(surveyDataDao.getSurvey())
    }

    suspend fun getSurveyById(plotId:String): SurveyData? {
      return  surveyDataDao.getSurveyByPlotId(plotId)
    }

    suspend fun deleteSurveyById(plotId:String) {
       surveyDataDao.deleteSurveyByPlotId(plotId)
    }




   suspend fun UpdateReport(token: String?, reportRequest: ReportRequest){
       if(networkUtils.isNetworkConnectionAvailable()){
           _updateReportState.postValue(NetworkSealed.Loading())
           val result = apiService.getUpdateReport("Bearer $token", reportRequest)
           if(result.isSuccessful&& result.code()==201 && result.body()!= null){
               _updateReportState.postValue(NetworkSealed.Data(result.body()))
           }else{
               _updateReportState.postValue(NetworkSealed.Error(null,Utils.parseErrorJson(result.errorBody())))
           }
       }
       else{
           _updateReportState.postValue(NetworkSealed.Error(null,"Internet not available!!"))
       }
   }
}