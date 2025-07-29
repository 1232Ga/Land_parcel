package com.example.land_parcel.PDFReport.Interface

import com.example.land_parcel.PDFReport.Model.LandSurveyRequest
import com.example.land_parcel.PDFReport.Response.LandSurveyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("land-survey/report")
    suspend fun sendSurveyReport(@Body request: LandSurveyRequest): Response<LandSurveyResponse>
}