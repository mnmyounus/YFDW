package com.mnmyounus.yfdw.domain.model

/**
 * PRIVACY_ANONYMITY: original spec — VPN or embedded Tor required, kill
 * switch armed, Tor service started.
 *
 * STANDARD_SECURE: encryption / scoped storage / malware check stay on,
 * but there's no network requirement — downloads run with or without a VPN.
 */
enum class OperatingMode {
    PRIVACY_ANONYMITY,
    STANDARD_SECURE
}
