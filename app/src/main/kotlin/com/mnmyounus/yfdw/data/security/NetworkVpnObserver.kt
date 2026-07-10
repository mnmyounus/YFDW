package com.mnmyounus.yfdw.data.security

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Emits true whenever the currently active network has an active VPN
 * transport (NetworkCapabilities.TRANSPORT_VPN). Re-evaluates on every
 * onCapabilitiesChanged callback, not just once at startup, so a VPN
 * dropping mid-download is caught immediately.
 */
class NetworkVpnObserver(private val context: Context) {

    fun observeVpnActive(): Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(ConnectivityManager::class.java)

        fun hasVpn(caps: NetworkCapabilities?): Boolean =
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(hasVpn(caps))
            }

            override fun onLost(network: Network) {
                trySend(hasVpn(cm?.getNetworkCapabilities(cm.activeNetwork)))
            }

            override fun onAvailable(network: Network) {
                trySend(hasVpn(cm?.getNetworkCapabilities(network)))
            }
        }

        // Initial value immediately, so subscribers don't wait for a change event.
        trySend(hasVpn(cm?.getNetworkCapabilities(cm.activeNetwork)))

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm?.registerNetworkCallback(request, callback)

        awaitClose { cm?.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
