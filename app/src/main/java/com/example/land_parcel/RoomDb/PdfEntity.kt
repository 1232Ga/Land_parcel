package com.example.offlinemapshow.RoomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_table")
data class PdfEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var filePath: String,
    var fileName: String,
    var isSynced: Boolean = false, // Flag to track sync with AWS S3
    var s3Url: String? = null
)
