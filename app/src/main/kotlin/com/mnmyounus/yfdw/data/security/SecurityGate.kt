package com.mnmyounus.yfdw.data.security

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.model.SecurityState
import com.mnmyounus.yfdw.domain.repository.NetworkSecurityRepository
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import kotlin.system.exitProcess

/**
 * Single source of truth for the kill switch. Only arms in
 * PRIVACY_ANONYMITY mode — NetworkSecurityRepositoryImpl never emits
 * Insecure in STANDARD_SECURE mode. Drives the embedded Tor service
 * start/stop in lockstep with the chosen mode, too.
 *
 * Deliberately uses a Notification rather than an AlertDialog so this works
 * identically whether or not an Activity is in the foreground — Activity
 * dialogs need a window token and will crash if posted from the
 * Application/Service. The in-foreground Compose UI shows its own
 * InsecureExitDialog by observing the same SecurityState; that's cosmetic
 * on top of this, not a dependency of it.
 */
class SecurityGate(
    private val context: Context,
    private val securityRepository: NetworkSecurityRepository,
    private val operatingModeRepository: OperatingModeRepository,
    private val okHttpClient: OkHttpClient
) {
    private val scope = CoroutineScope(SupervisorJob())

    fun start() {
        scope.launch {
            operatingModeRepository.mode.distinctUntilChanged().collectLatest { mode ->
                if (mode == OperatingMode.PRIVACY_ANONYMITY) securityRepository.startTor()
                else securityRepository.stopTor()
            }
        }

        scope.launch {
            securityRepository.securityState.collectLatest { state ->
                if (state is SecurityState.Insecure) killSecurely()
            }
        }
    }

    private suspend fun killSecurely() {
        postExitNotification()
        delay(KILL_DELAY_MS)
        okHttpClient.dispatcher.cancelAll()
        okHttpClient.connectionPool.evictAll()
        exitProcess(0)
    }

    private fun postExitNotification() {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Security", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("Insecure network detected")
            .setContentText("No VPN or Tor route found. YFDW is exiting to protect your privacy.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        nm.notify(EXIT_NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "yfdw_security"
        private const val EXIT_NOTIFICATION_ID = 9001
        private const val KILL_DELAY_MS = 3000L
    }
}
