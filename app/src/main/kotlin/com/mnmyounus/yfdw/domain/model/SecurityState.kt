package com.mnmyounus.yfdw.domain.model

sealed interface SecurityState {
    /** STANDARD_SECURE mode: always secure, network requirement intentionally skipped. */
    data object SecureStandard : SecurityState

    /** PRIVACY_ANONYMITY mode, VPN transport detected. */
    data object SecureVpn : SecurityState

    /** PRIVACY_ANONYMITY mode, embedded Tor bootstrapped and routing. */
    data object SecureTor : SecurityState

    /** PRIVACY_ANONYMITY mode, neither VPN nor Tor present — triggers the kill switch. */
    data object Insecure : SecurityState

    /** Operating mode hasn't been chosen yet (first launch, onboarding in progress). */
    data object Evaluating : SecurityState
}
