package com.mnmyounus.yfdw.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnmyounus.yfdw.domain.model.DownloadItem
import com.mnmyounus.yfdw.domain.usecase.CancelDownloadUseCase
import com.mnmyounus.yfdw.domain.usecase.EnqueueDownloadUseCase
import com.mnmyounus.yfdw.domain.usecase.ObserveDownloadsUseCase
import com.mnmyounus.yfdw.domain.usecase.PauseResumeDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadListViewModel @Inject constructor(
    observeDownloads: ObserveDownloadsUseCase,
    private val enqueueDownload: EnqueueDownloadUseCase,
    private val pauseResumeDownload: PauseResumeDownloadUseCase,
    private val cancelDownload: CancelDownloadUseCase
) : ViewModel() {

    val downloads: StateFlow<List<DownloadItem>> = observeDownloads()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addDownload(url: String, fileName: String) {
        viewModelScope.launch { enqueueDownload(url, fileName) }
    }

    fun pause(id: Long) = viewModelScope.launch { pauseResumeDownload.pause(id) }
    fun resume(id: Long) = viewModelScope.launch { pauseResumeDownload.resume(id) }
    fun cancel(id: Long) = viewModelScope.launch { cancelDownload(id) }
}
