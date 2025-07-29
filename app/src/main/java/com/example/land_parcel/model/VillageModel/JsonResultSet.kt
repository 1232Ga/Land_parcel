package com.example.land_parcel.model.VillageModel

data class JsonResultSet(
    var FirstName: String,
    var LastName: String,
    var MobileNo: String,
    var UserEmail: String,
    var UserId: String,
    var Villages: List<Village>
)