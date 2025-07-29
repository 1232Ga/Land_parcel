package com.example.land_parcel.model.data

import com.google.gson.annotations.SerializedName

data class GeoJson(
    @SerializedName("type") val type: String,
    @SerializedName("features") val features: List<Feature>
)

data class Feature(
    @SerializedName("type") val type: String,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("properties") val properties: Properties
)

data class Geometry(
    @SerializedName("type") val type: String,
    @SerializedName("coordinates") val coordinates: List<List<List<List<Double>>>>
)

data class Properties(
    @SerializedName("Land_Type") val landType: String,
    @SerializedName("Village_Na") val villageName: String,
    @SerializedName("Dist_Name") val districtName: String,
    @SerializedName("Block") val block: String,
    @SerializedName("Owner_Name") val ownerName: String,
    @SerializedName("Parcel_Id") val parcelId: String,
    @SerializedName("unique_cod") val uniqueCode: Int
)
