package com.mnmyounus.yfdw.domain.usecase

import com.mnmyounus.yfdw.domain.repository.DownloadRepository
import javax.inject.Inject

class EnqueueDownloadUseCase @Inject constructor(
    private val repository: DownloadRepository
) {
    suspend operator fun invoke(url: String, fileName: String) = repository.enqueue(url, fileName)
}
