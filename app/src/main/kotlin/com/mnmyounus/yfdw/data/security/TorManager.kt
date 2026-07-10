package com.mnmyounus.yfdw.data.security

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.mnmyounus.yfdw.domain.model.TorStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Wraps Guardian Project's org.torproject.jni.TorService (tor-android 0.4.9.8).
 *
 * NOTE: If Tor broadcasts don't fire or the service won't bind, verify these
 * constant names against your specific tor-android release. The values below
 * are correct for 0.4.9.8; older/newer versions may differ.
 */
class TorManager(private val context: Context) {

    private val _status = MutableStateFlow(TorStatus.STOPPED)
    val status: StateFlow<TorStatus> = _status.asStateFlow()

    private var bound = false
    private var receiverRegistered = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            bound = true
            _status.value = TorStatus.STARTING
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            _status.value = TorStatus.STOPPED
        }
    }

    private val statusReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            try {
                val status = intent?.getStringExtra("status") ?: return
                _status.value = when (status) {
                    "on" -> TorStatus.CONNECTED
                    "starting" -> TorStatus.STARTING
                    "stopping", "off" -> TorStatus.STOPPED
                    else -> return
                }
            } catch (e: Exception) {
                // Silently continue — some versions don't broadcast status
            }
        }
    }

    fun start() {
        if (bound) return
        try {
            if (!receiverRegistered) {
                ContextCompat.registerReceiver(
                    context,
                    statusReceiver,
                    IntentFilter("org.torproject.android.intent.action.STATUS"),
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
                receiverRegistered = true
            }
        } catch (e: Exception) {
            // Receiver registration failed; continue anyway
        }

        try {
            val torServiceClass = Class.forName("org.torproject.jni.TorService")
            val intent = Intent(context, torServiceClass)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            context.startService(intent)
        } catch (e: Exception) {
            _status.value = TorStatus.FAILED
        }
    }

    fun stop() {
        if (bound) {
            runCatching { context.unbindService(connection) }
            bound = false
        }
        if (receiverRegistered) {
            runCatching { context.unregisterReceiver(statusReceiver) }
            receiverRegistered = false
        }
        try {
            val torServiceClass = Class.forName("org.torproject.jni.TorService")
            val intent = Intent(context, torServiceClass)
            context.stopService(intent)
        } catch (e: Exception) {
            // Silently continue
        }
        _status.value = TorStatus.STOPPED
    }

    /** Only non-null once Tor has finished bootstrapping. */
    fun socksProxyOrNull(): Proxy? =
        if (_status.value == TorStatus.CONNECTED) {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress("127.0.0.1", SOCKS_PORT))
        } else null

    companion object {
        const val SOCKS_PORT = 9050
    }
}
