package com.example.land_parcel.model.VillageModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "villages")
data class Village(

    var DistrictName: String,
    var StateName: String,
    var TehsilName: String,
    var UserVillageMappingId: String,
    @PrimaryKey
    var VillageId: String,
    var VillageName: String
)