package com.mnmyounus.yfdw.domain.repository

import com.mnmyounus.yfdw.domain.model.OperatingMode
import kotlinx.coroutines.flow.Flow

interface OperatingModeRepository {
    /** null = no mode chosen yet (first launch, show the mode picker). */
    val mode: Flow<OperatingMode?>
    suspend fun setMode(mode: OperatingMode)
}
