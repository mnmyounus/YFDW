package com.mnmyounus.yfdw.domain.usecase

import com.mnmyounus.yfdw.domain.repository.DownloadRepository
import javax.inject.Inject

class ObserveDownloadsUseCase @Inject constructor(
    private val repository: DownloadRepository
) {
    operator fun invoke() = repository.downloads
}
