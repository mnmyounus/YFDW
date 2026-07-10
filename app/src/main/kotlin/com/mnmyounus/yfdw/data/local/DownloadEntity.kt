package com.mnmyounus.yfdw.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val url: String,
    val fileName: String,
    val destinationUri: String,
    val totalBytes: Long = -1L,
    val downloadedBytes: Long = 0L,
    val chunkCount: Int = 1,
    val status: String,
    val sha256: String? = null,
    val createdAt: Long
)
