package com.example.land_parcel.model.VillageModel

data class VillageItem(val villageId: String, val villageName: String) {
    override fun toString(): String {
        return villageName // This ensures only villageName is displayed in the Spinner
    }
}

