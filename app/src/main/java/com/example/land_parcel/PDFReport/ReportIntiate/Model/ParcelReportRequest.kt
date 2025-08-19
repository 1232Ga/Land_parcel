package com.example.land_parcel.PDFReport.ReportIntiate.Model

data class ParcelReportRequest(
    val khasra_no: Int,
    val village_id: String,
    val village_name: String,
    val district_name: String,
    val area: String,
    val tehsil: String,
    val owner: String,
    val govt_id: String,
    val father_name: String,
    val land_type: String,
    val mobile_no: String,
    val pnil_no: String,
    val house_no: String,
    val block: String,
    val Land_parcel_updated_on: String
)
