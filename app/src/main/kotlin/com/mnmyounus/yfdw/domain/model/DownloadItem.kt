package com.mnmyounus.yfdw.domain.model

data class DownloadItem(
    val id: Long = 0L,
    val url: String,
    val fileName: String,
    val destinationUri: String,
    val totalBytes: Long = -1L,
    val downloadedBytes: Long = 0L,
    val chunkCount: Int = 1,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val sha256: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    QUEUED, RUNNING, PAUSED, COMPLETED, FAILED, BLOCKED_MALWARE, CANCELLED
}
