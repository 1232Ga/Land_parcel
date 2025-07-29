package com.example.land_parcel.model.villageGeoJson

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VillageGeoJson(
    @PrimaryKey
    var VillageId: String,
    @ColumnInfo(name = "villageJson") var villageJson: String?,
    @ColumnInfo(name = "changesJson") var changesJson: String?,
    var bBoxJson: String?,
)
