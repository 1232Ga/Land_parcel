package com.example.land_parcel.PDFReport.ReportIntiate.Interface

import com.example.land_parcel.PDFReport.ReportIntiate.Model.ParcelReportRequest
import com.example.land_parcel.PDFReport.ReportIntiate.Response.ReportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("report/parcelreport")
    fun uploadParcelReport(@Header("Authorization") token: String?,
                           @Body request: ParcelReportRequest): Call<ReportResponse>
}
