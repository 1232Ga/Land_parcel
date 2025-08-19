package com.example.land_parcel.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.land_parcel.PDFReport.ReportModel.Response.ApiResponse
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.Utils
import com.example.land_parcel.db.LandDatabase
import com.example.land_parcel.di.qualifiers.LandQualifiers
import com.example.land_parcel.model.VillageModel.Village
import com.example.land_parcel.model.VillageModel.VillageData
import com.example.land_parcel.model.VillageModel.VillageRoot
import com.example.land_parcel.model.survey.SurveyData
import com.example.land_parcel.network.ApiService
import com.example.land_parcel.network.NetworkSealed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardRepository @Inject constructor(landDatabase: LandDatabase,@LandQualifiers.VisualizationRetrofit val apiService: ApiService,val networkUtils: NetworkUtils) {
    private val surveyDataDao = landDatabase.getSurveyDataDao()
    private val villageDataDao =landDatabase.getVillageDataDao()

    private val _surveyData = MutableLiveData<List<SurveyData>>()
    val surveyData: LiveData<List<SurveyData>> get() = _surveyData

    //for village list
    private val _villageResponse = MutableLiveData<NetworkSealed<List<Village>>>()
    val villageResponse: LiveData<NetworkSealed<List<Village>>> get() = _villageResponse

    suspend fun getSurveyData() {
        _surveyData.postValue(surveyDataDao.getSurvey())
    }

    suspend fun getvillage(token: String,userid: String){
         if(networkUtils.isNetworkConnectionAvailable()){
             _villageResponse.postValue(NetworkSealed.Loading())
             val result= apiService.getVillageList("Bearer "+token,userid)
             if(result.isSuccessful && result.code()==200 && result.body() != null){
                 villageDataDao.insertVillages(result.body()?.Data?.JsonResultSet?.flatMap { it.Villages }!!)
                 _villageResponse.postValue(NetworkSealed.Data(result.body()?.Data?.JsonResultSet?.flatMap { it.Villages }))
             }else{
                 _villageResponse.postValue(NetworkSealed.Error(null, Utils.parseErrorJson(result.errorBody())))
             }
         }
        else{
                _villageResponse.postValue(NetworkSealed.Data(villageDataDao.getAllVillages()))
         }
    }


    suspend fun getParcelReport(villageId: String?, khasraNumber: String,token :String): Result<ApiResponse> {
        return try {
            val response = apiService.getLandParcelReport(token,villageId, khasraNumber)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty Response"))
            }
            else {
                Result.failure(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

}