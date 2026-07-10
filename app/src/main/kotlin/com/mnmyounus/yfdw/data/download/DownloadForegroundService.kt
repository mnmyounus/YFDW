package com.mnmyounus.yfdw.data.download

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.core.content.pm.ServiceInfoCompat
import com.mnmyounus.yfdw.data.local.DownloadDao
import com.mnmyounus.yfdw.domain.model.DownloadStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Foreground service that runs one download at a time. Each enqueued download
 * triggers a new service intent. Jobs are stored in a registry so pause/cancel
 * can terminate the actual work instead of just relabeling the DB status.
 */
@AndroidEntryPoint
class DownloadForegroundService : Service() {

    @Inject lateinit var downloadEngine: DownloadEngine
    @Inject lateinit var downloadDao: DownloadDao
    @Inject lateinit var notificationHelper: DownloadNotificationHelper

    private val supervisor = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + supervisor)
    private val jobRegistry = mutableMapOf<Long, Job>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val downloadId = intent?.getLongExtra(EXTRA_DOWNLOAD_ID, -1L) ?: -1L
        if (downloadId == -1L) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Cancel any previous job for this download (e.g., resume after pause)
        jobRegistry[downloadId]?.cancel()

        ServiceCompat.startForeground(
            this,
            DownloadNotificationHelper.notificationId(downloadId),
            notificationHelper.progressNotification("Starting…", 0, true),
            ServiceInfoCompat.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )

        val job = scope.launch {
            try {
                val entity = downloadDao.getById(downloadId)
                if (entity == null) {
                    stopSelf()
                    return@launch
                }
                downloadDao.updateStatus(downloadId, DownloadStatus.RUNNING.name)

                val result = downloadEngine.run(
                    url = entity.url,
                    workDir = File(filesDir, "downloads/$downloadId"),
                    finalFile = File(filesDir, "downloads/${entity.fileName}")
                ) { downloaded, total ->
                    val percent = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                    downloadDao.updateProgress(downloadId, downloaded, total)
                }

                downloadDao.updateHash(downloadId, result.sha256)
                val finalStatus = if (result.blocked) DownloadStatus.BLOCKED_MALWARE else DownloadStatus.COMPLETED
                downloadDao.updateStatus(downloadId, finalStatus.name)
            } catch (e: Exception) {
                downloadDao.updateStatus(downloadId, DownloadStatus.FAILED.name)
            } finally {
                jobRegistry.remove(downloadId)
                stopSelf()
            }
        }
        jobRegistry[downloadId] = job
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        supervisor.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_DOWNLOAD_ID = "extra_download_id"
    }
}
