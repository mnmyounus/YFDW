package com.mnmyounus.yfdw.data.download

import android.content.Context
import android.content.Intent
import com.mnmyounus.yfdw.data.local.DownloadDao
import com.mnmyounus.yfdw.data.local.DownloadEntity
import com.mnmyounus.yfdw.domain.model.DownloadItem
import com.mnmyounus.yfdw.domain.model.DownloadStatus
import com.mnmyounus.yfdw.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dao: DownloadDao
) : DownloadRepository {

    override val downloads: Flow<List<DownloadItem>> = dao.observeAll().map { list ->
        list.map {
            DownloadItem(
                id = it.id, url = it.url, fileName = it.fileName,
                destinationUri = it.destinationUri, totalBytes = it.totalBytes,
                downloadedBytes = it.downloadedBytes, chunkCount = it.chunkCount,
                status = DownloadStatus.valueOf(it.status), sha256 = it.sha256,
                createdAt = it.createdAt
            )
        }
    }

    override suspend fun enqueue(url: String, fileName: String): Long {
        val id = dao.insert(
            DownloadEntity(
                url = url, fileName = fileName, destinationUri = fileName,
                status = DownloadStatus.QUEUED.name, createdAt = System.currentTimeMillis()
            )
        )
        startService(id)
        return id
    }

    override suspend fun pause(id: Long) {
        dao.updateStatus(id, DownloadStatus.PAUSED.name)
        // See DownloadForegroundService note: the running job isn't actually
        // cancelled yet, so this currently just relabels status in the UI.
    }

    override suspend fun resume(id: Long) = startService(id)

    override suspend fun cancel(id: Long) {
        dao.updateStatus(id, DownloadStatus.CANCELLED.name)
    }

    private fun startService(id: Long) {
        val intent = Intent(context, DownloadForegroundService::class.java)
            .putExtra(DownloadForegroundService.EXTRA_DOWNLOAD_ID, id)
        context.startForegroundService(intent)
    }
}
