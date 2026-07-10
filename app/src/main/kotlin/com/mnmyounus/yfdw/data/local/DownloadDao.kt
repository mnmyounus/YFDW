package com.mnmyounus.yfdw.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getById(id: Long): DownloadEntity?

    @Insert
    suspend fun insert(entity: DownloadEntity): Long

    @Update
    suspend fun update(entity: DownloadEntity)

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE downloads SET downloadedBytes = :bytes, totalBytes = :total WHERE id = :id")
    suspend fun updateProgress(id: Long, bytes: Long, total: Long)

    @Query("UPDATE downloads SET sha256 = :sha256 WHERE id = :id")
    suspend fun updateHash(id: Long, sha256: String)
}
