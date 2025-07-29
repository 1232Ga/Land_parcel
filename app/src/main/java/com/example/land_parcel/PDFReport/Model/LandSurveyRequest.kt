package com.example.land_parcel.PDFReport.Model

data class LandSurveyRequest(
    val district_name: String,
    val tehsil: String,
    val village_name: String,
    val village_id: String,
    val unique_id: String,
    val house_no: String,
    val owner_name: String,
    val land_type: String,
    val mobile_no: String,
    val govt_id: String,
    val block: String,
    val land_use: String,
    val area: String,
    val latitude: String,
    val longitude: String,
    val pnil_no: String,
    val date: String,
    val user_id: String,
    val document: String,
    val remark: String
)


