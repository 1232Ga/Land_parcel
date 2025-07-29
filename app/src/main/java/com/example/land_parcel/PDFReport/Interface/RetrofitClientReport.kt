package com.example.land_parcel.PDFReport.Interface

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClientReport {
    private const val BASE_URL = "https://geoserver.bluehawk.ai:8045/"
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Connection timeout
        .readTimeout(60, TimeUnit.SECONDS)     // Read timeout
        .writeTimeout(60, TimeUnit.SECONDS)    // Write timeout
        .build()
    val instance: ReportService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ReportService::class.java)
    }
}