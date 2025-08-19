package com.example.land_parcel.model.Pnil

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pnil_table")
data class PnilEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pnilNo: String?
)


