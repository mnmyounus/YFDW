package com.mnmyounus.yfdw.data.settings

import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OperatingModeRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : OperatingModeRepository {

    override val mode: Flow<OperatingMode?> = dataStore.mode.map { stored ->
        stored?.let { runCatching { OperatingMode.valueOf(it) }.getOrNull() }
    }

    override suspend fun setMode(mode: OperatingMode) {
        dataStore.setMode(mode.name)
    }
}
