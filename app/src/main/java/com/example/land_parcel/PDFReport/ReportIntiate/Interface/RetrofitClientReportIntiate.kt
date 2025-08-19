package com.example.land_parcel.PDFReport.ReportIntiate.Interface

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientReportIntiate {
    private const val BASE_URL = "https://ls-stage-visualization-service.bluehawk.ai/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
