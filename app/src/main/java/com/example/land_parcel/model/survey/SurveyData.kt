package com.example.land_parcel.model.survey

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SurveyData(
    val Block: String,
    val DistName: String,
    val HouseNo: String,
    val LandType: String,
    val MobileNo: String,
    val Owner: String,
    val Latitude: String,
    val Longitude: String,
    val Area:String,
    val Tehsil: String,
    val VillName: String,
    val Village_Id: String,
    val featureid:String,
    val SurveyDate:String,
    val Remark:String,
    @PrimaryKey
    val Khasra_No: String,
    val photos: List<Uri>,
    var filePath: String,
    var fileName: String,
    var PNIL_No: String,
    var GovtID: String,
    var Father_na: String,
)