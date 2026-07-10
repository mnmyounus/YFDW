package com.mnmyounus.yfdw.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnmyounus.yfdw.domain.model.OperatingMode
import com.mnmyounus.yfdw.domain.repository.OperatingModeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: OperatingModeRepository
) : ViewModel() {

    val mode: StateFlow<OperatingMode?> = repository.mode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun setMode(mode: OperatingMode) {
        viewModelScope.launch { repository.setMode(mode) }
    }
}
