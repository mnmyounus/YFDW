package com.mnmyounus.yfdw.domain.usecase

import com.mnmyounus.yfdw.domain.repository.DownloadRepository
import javax.inject.Inject

class CancelDownloadUseCase @Inject constructor(
    private val repository: DownloadRepository
) {
    suspend operator fun invoke(id: Long) = repository.cancel(id)
}
