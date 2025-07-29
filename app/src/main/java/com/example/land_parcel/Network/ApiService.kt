package com.example.land_parcel.network

import com.example.land_parcel.model.ChangePassword.ChangePasswordRequest
import com.example.land_parcel.model.ChangePassword.ChangePasswordResponse
import com.example.land_parcel.model.Logout.LogoutRequest
import com.example.land_parcel.model.Logout.LogoutResponse
import com.example.land_parcel.model.VillageModel.VillageRoot
import com.example.land_parcel.model.login.LoginRequest
import com.example.land_parcel.model.login.LoginResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiService {
    //Login User
    @POST("login")
    suspend fun getLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

  //Logout User
    @POST("logout")
    suspend fun getLogout(@Body logoutRequest: LogoutRequest):Response<LogoutResponse>

//Change Password
   @PUT("user/changepassword/{userId}")
    suspend fun changePassword(@Header("Authorization") token: String?,
                                  @Path("userId") userId: String?,
                                  @Body changePasswordRequest: ChangePasswordRequest):Response<ChangePasswordResponse>

    //Village List
    @GET("get/uservillage/mapping/by/{userId}")
    suspend fun getVillageList(@Header("Authorization") token: String?,
                               @Path("userId") userId: String?):Response<VillageRoot>


    //Sync Data
    @Headers("Content-Type: application/xml", "Accept: application/xml")
    @POST
    fun sendWfsTransaction(@Url url: String, @Body body: RequestBody): Call<ResponseBody>
}