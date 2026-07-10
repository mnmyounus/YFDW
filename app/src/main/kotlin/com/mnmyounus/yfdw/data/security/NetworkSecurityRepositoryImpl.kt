package com.mnmyounus.yfdw.data.security

import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.model.SecurityState
import com.mnmyounus.yfdw.domain.model.TorStatus
import com.mnmyounus.yfdw.domain.repository.NetworkSecurityRepository
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class NetworkSecurityRepositoryImpl @Inject constructor(
    private val vpnObserver: NetworkVpnObserver,
    private val torManager: TorManager,
    private val operatingModeRepository: OperatingModeRepository
) : NetworkSecurityRepository {

    override val securityState = combine(
        operatingModeRepository.mode,
        vpnObserver.observeVpnActive(),
        torManager.status
    ) { mode, vpnActive, torStatus ->
        when (mode) {
            null -> SecurityState.Evaluating
            OperatingMode.STANDARD_SECURE -> SecurityState.SecureStandard
            OperatingMode.PRIVACY_ANONYMITY -> when {
                vpnActive -> SecurityState.SecureVpn
                torStatus == TorStatus.CONNECTED -> SecurityState.SecureTor
                else -> SecurityState.Insecure
            }
        }
    }.distinctUntilChanged()

    override fun startTor() = torManager.start()
    override fun stopTor() = torManager.stop()
}
