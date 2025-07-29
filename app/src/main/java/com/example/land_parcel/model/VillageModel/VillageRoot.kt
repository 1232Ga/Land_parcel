package com.example.land_parcel.model.VillageModel

data class VillageRoot(
    var ApiVersion: String,
    var Data: VillageData,
    var Errors: List<Any>,
    var Success: Boolean
)