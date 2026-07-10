package com.mnmyounus.yfdw.data.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import javax.inject.Inject

class DownloadNotificationHelper @Inject constructor(
    private val context: Context
) {
    init {
        val nm = context.getSystemService(NotificationManager::class.java)
        if (nm?.getNotificationChannel(CHANNEL_ID) == null) {
            nm?.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    fun progressNotification(fileName: String, progress: Int, indeterminate: Boolean) =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(fileName)
            .setProgress(100, progress, indeterminate)
            .setOngoing(true)
            .build()

    fun completeNotification(fileName: String, blocked: Boolean) =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(fileName)
            .setContentText(if (blocked) "Blocked — matched a known-malicious signature" else "Download complete")
            .setAutoCancel(true)
            .build()

    companion object {
        const val CHANNEL_ID = "yfdw_downloads"
        fun notificationId(downloadId: Long) = 10_000 + downloadId.toInt()
    }
}
