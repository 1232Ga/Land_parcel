package com.example.land_parcel.PDFReport.ReportIntiate.Response

data class ReportResponse(
    val ApiVersion: String,
    val Success: Boolean,
    val Message: String,
    val Data: String,
    val Errors: List<String>)

