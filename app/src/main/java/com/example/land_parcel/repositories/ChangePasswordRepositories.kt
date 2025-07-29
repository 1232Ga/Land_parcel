package com.example.land_parcel.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobile.client.results.Token
import com.example.land_parcel.Utils.NetworkUtils
import com.example.land_parcel.Utils.Utils
import com.example.land_parcel.di.qualifiers.LandQualifiers
import com.example.land_parcel.model.ChangePassword.ChangePasswordRequest
import com.example.land_parcel.model.ChangePassword.ChangePasswordResponse
import com.example.land_parcel.model.Logout.LogoutRequest
import com.example.land_parcel.model.Logout.LogoutResponse
import com.example.land_parcel.model.login.LoginRequest
import com.example.land_parcel.network.ApiService
import com.example.land_parcel.network.NetworkSealed
import javax.inject.Inject

class ChangePasswordRepositories @Inject constructor(@LandQualifiers.UserRetrofit  private val apiService: ApiService, private val networkUtils: NetworkUtils) {

    private val _chnagepassResponse=MutableLiveData<NetworkSealed<ChangePasswordResponse>>()
    val changepassResponse:LiveData<NetworkSealed<ChangePasswordResponse>> get()=_chnagepassResponse

    suspend fun changepassword(token: String,userid:String,changePasswordRequest: ChangePasswordRequest){
        if(networkUtils.isNetworkConnectionAvailable()){
            _chnagepassResponse.postValue(NetworkSealed.Loading())
            val result=apiService.changePassword("Bearer "+token,userid,changePasswordRequest)
            if(result.isSuccessful && result.code()==200 && result.body()!=null){
                _chnagepassResponse.postValue(NetworkSealed.Data(result.body()))

            }else{
                _chnagepassResponse.postValue(NetworkSealed.Error(null, Utils.parseErrorJson(result.errorBody())))
            }
        }else{
            _chnagepassResponse.postValue(NetworkSealed.Error(null, "Internet not available!!"))
        }



    }

}