package com.mnmyounus.yfdw.domain.usecase

import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import javax.inject.Inject

class SetOperatingModeUseCase @Inject constructor(
    private val repository: OperatingModeRepository
) {
    suspend operator fun invoke(mode: OperatingMode) = repository.setMode(mode)
}
