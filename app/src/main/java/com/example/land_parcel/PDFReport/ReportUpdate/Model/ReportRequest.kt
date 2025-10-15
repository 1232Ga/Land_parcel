package com.example.land_parcel.PDFReport.ReportUpdate.Model

data class ReportRequest (
    val khasra_number: String,
    val village_id: String?,
    val status: Int,
    val land_parcel_updated_on : String,
    val owner : String,
    val father_name : String,
    val village_name : String,
    val district_name : String,
    val tehsil : String,
    val govt_id : String,
    val land_type : String,
    val area : String,
    val mobile_no : String,
    val pnil_no : String?,
    val house_no : String,
    val block : String,
    val remark : String)
