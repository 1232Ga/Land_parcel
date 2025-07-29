package com.example.land_parcel.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.Utils
import com.example.land_parcel.di.qualifiers.LandQualifiers
import com.example.land_parcel.model.Logout.LogoutRequest
import com.example.land_parcel.model.Logout.LogoutResponse
import com.example.land_parcel.model.login.LoginRequest
import com.example.land_parcel.network.ApiService
import com.example.land_parcel.network.NetworkSealed
import javax.inject.Inject

class LogoutRepositories @Inject constructor(@LandQualifiers.BaseRetrofit  private val apiService: ApiService, private val networkUtils: NetworkUtils) {

    private val _LogoutResponse=MutableLiveData<NetworkSealed<LogoutResponse>>()
    val LogoutResponse:LiveData<NetworkSealed<LogoutResponse>> get()=_LogoutResponse

    suspend fun logout(logoutRequest: LogoutRequest){
        if(networkUtils.isNetworkConnectionAvailable()){
            _LogoutResponse.postValue(NetworkSealed.Loading())
            val result=apiService.getLogout(logoutRequest)
            if(result.isSuccessful && result.code()==200 && result.body()!=null){
                _LogoutResponse.postValue(NetworkSealed.Data(result.body()))

            }else{
                _LogoutResponse.postValue(NetworkSealed.Error(null, Utils.parseErrorJson(result.errorBody())))
            }
        }else{
            _LogoutResponse.postValue(NetworkSealed.Error(null, "Internet not available!!"))
        }



    }




}