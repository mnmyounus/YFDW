package com.mnmyounus.yfdw.presentation.gate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.model.SecurityState
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import com.mnmyounus.yfdw.domain.usecase.ObserveSecurityStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityGateViewModel @Inject constructor(
    private val operatingModeRepository: OperatingModeRepository,
    observeSecurityState: ObserveSecurityStateUseCase
) : ViewModel() {

    /** False until DataStore emits its first value — even an unset mode counts as "loaded". */
    val modeLoaded: StateFlow<Boolean> = operatingModeRepository.mode
        .map { true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val mode: StateFlow<OperatingMode?> = operatingModeRepository.mode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val securityState: StateFlow<SecurityState> = observeSecurityState()
        .stateIn(viewModelScope, SharingStarted.Eagerly, SecurityState.Evaluating)

    fun setMode(mode: OperatingMode) {
        viewModelScope.launch { operatingModeRepository.setMode(mode) }
    }
}
