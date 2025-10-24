package com.example.land_parcel.Network

import com.example.land_parcel.model.ParcelCount.ParcelModelClass
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LandParcelCountInterface {
    @GET("land-survey/layers_count")
    suspend fun getLayerCount(
        @Query("village_id") villageId: String,
        @Query("village_name") villageName: String
    ): Response<ParcelModelClass>
}
