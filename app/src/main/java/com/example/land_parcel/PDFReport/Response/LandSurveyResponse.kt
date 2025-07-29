package com.example.land_parcel.PDFReport.Response

data class LandSurveyResponse(
    val workspace: String,
    val status: String,
    val message: String,
    val report_link: String
)

