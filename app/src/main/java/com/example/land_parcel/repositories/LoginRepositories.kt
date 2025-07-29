package com.example.land_parcel.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.Utils
import com.example.land_parcel.di.qualifiers.LandQualifiers
import com.example.land_parcel.model.login.LoginRequest
import com.example.land_parcel.model.login.LoginResponse
import com.example.land_parcel.network.ApiService
import com.example.land_parcel.network.NetworkSealed
import javax.inject.Inject

class LoginRepositories @Inject constructor(@LandQualifiers.BaseRetrofit private val apiService: ApiService, private val networkUtils: NetworkUtils) {

    private val _loginResponse=MutableLiveData<NetworkSealed<LoginResponse>>()
    val loginResponse:LiveData<NetworkSealed<LoginResponse>> get()=_loginResponse

    suspend fun login(loginRequest: LoginRequest){
        if(networkUtils.isNetworkConnectionAvailable()){
            _loginResponse.postValue(NetworkSealed.Loading())
            val result=apiService.getLogin(loginRequest)
            if(result.isSuccessful && result.code()==200 && result.body()!=null){
                _loginResponse.postValue(NetworkSealed.Data(result.body()))

            }else{
                _loginResponse.postValue(NetworkSealed.Error(null, Utils.parseErrorJson(result.errorBody())))
            }
        }
        else{
            _loginResponse.postValue(NetworkSealed.Error(null, "Internet not available!!"))
        }



    }




}