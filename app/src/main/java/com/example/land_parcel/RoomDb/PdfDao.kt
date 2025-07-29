package com.example.offlinemapshow.RoomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PdfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdfEntity: PdfEntity)

    @Query("SELECT * FROM pdf_table WHERE filePath = :path LIMIT 1")
    suspend fun getPdfByPath(path: String): PdfEntity?

    @Query("SELECT * FROM pdf_table WHERE isSynced = 0")
    suspend fun getUnsyncedPdfs(): List<PdfEntity>

    @Update
    suspend fun update(pdfEntity: PdfEntity)
}
