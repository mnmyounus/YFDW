package com.mnmyounus.yfdw.domain.repository

import com.mnmyounus.yfdw.domain.model.SecurityState
import kotlinx.coroutines.flow.Flow

interface NetworkSecurityRepository {
    val securityState: Flow<SecurityState>
    fun startTor()
    fun stopTor()
}
