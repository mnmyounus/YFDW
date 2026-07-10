package com.mnmyounus.yfdw.domain.usecase

import com.mnmyounus.yfdw.domain.repository.NetworkSecurityRepository
import javax.inject.Inject

class ObserveSecurityStateUseCase @Inject constructor(
    private val repository: NetworkSecurityRepository
) {
    operator fun invoke() = repository.securityState
}
