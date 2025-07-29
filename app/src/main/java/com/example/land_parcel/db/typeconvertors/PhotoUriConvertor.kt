package com.example.land_parcel.db.typeconvertors

import android.net.Uri
import android.util.Base64
import androidx.room.TypeConverter
import com.google.gson.Gson

class PhotoUriConvertor {
    @TypeConverter
    fun fromListIntToString(intList: List<Uri>): String = intList.toString()
    @TypeConverter
    fun toListIntFromString(stringList: String): List<Uri> {
        val result = ArrayList<Uri>()
        val split =stringList.replace("[","").replace("]","").replace(" ","").split(",")
        for (n in split) {
            try {
                result.add(Uri.parse(n))
            } catch (e: Exception) {

            }
        }
        return result
    }
}