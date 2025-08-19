package com.example.land_parcel.PDFReport.ReportUpdate.Model

data class ReportRequest (
    val khasra_number: String,
    val village_id: String?,
    val status: Int,
    val Land_parcel_updated_on : String)
