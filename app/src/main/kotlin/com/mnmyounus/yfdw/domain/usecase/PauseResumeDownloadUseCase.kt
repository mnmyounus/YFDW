package com.mnmyounus.yfdw.domain.usecase

import com.mnmyounus.yfdw.domain.repository.DownloadRepository
import javax.inject.Inject

class PauseResumeDownloadUseCase @Inject constructor(
    private val repository: DownloadRepository
) {
    suspend fun pause(id: Long) = repository.pause(id)
    suspend fun resume(id: Long) = repository.resume(id)
}
