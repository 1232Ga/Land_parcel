package com.example.land_parcel.db.typeconvertors

import android.util.Base64
import androidx.room.TypeConverter

class PhotoByteConvertor {
    @TypeConverter
    fun fromByteArrayList(byteArrays: List<ByteArray>?): String {
        return byteArrays?.joinToString(
            ",") { Base64.encodeToString(it, Base64.DEFAULT) } ?: ""
    }

    @TypeConverter
    fun toByteArrayList(data: String): List<ByteArray> {
        return if (data.isEmpty()) {
            emptyList()
        } else {
            data.split(",").map { Base64.decode(it, Base64.DEFAULT) }
        }
    }
}