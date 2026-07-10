package com.mnmyounus.yfdw.domain.repository

import com.mnmyounus.yfdw.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    val downloads: Flow<List<DownloadItem>>
    suspend fun enqueue(url: String, fileName: String): Long
    suspend fun pause(id: Long)
    suspend fun resume(id: Long)
    suspend fun cancel(id: Long)
}
