package com.example.land_parcel.di.modules
import com.example.land_parcel.Network.LandParcelCountInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientCount {
    private const val BASE_URL = "https://geoserver.bluehawk.ai:8045/"

    val instance: LandParcelCountInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LandParcelCountInterface::class.java)
    }
}
