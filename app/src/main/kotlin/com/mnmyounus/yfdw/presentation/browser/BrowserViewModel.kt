package com.mnmyounus.yfdw.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor() : ViewModel() {

    private val _currentUrl = MutableStateFlow("https://www.example.com")
    val currentUrl: StateFlow<String> = _currentUrl

    private val _detectedLinks = MutableStateFlow<List<DetectedLink>>(emptyList())
    val detectedLinks: StateFlow<List<DetectedLink>> = _detectedLinks

    fun loadUrl(url: String) {
        _currentUrl.value = url
    }

    fun extractLinks(pageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val document = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get()
                
                val links = mutableListOf<DetectedLink>()

                // Audio files
                document.select("a[href~=\\.(mp3|flac|wav|aac|ogg)$]").forEach {
                    val href = it.absUrl("href")
                    if (href.isNotEmpty()) {
                        links.add(DetectedLink(
                            title = it.text().takeIf { t -> t.isNotEmpty() } ?: href.substringAfterLast("/"),
                            url = href,
                            type = "AUDIO"
                        ))
                    }
                }

                // Video files
                document.select("a[href~=\\.(mp4|mkv|webm|avi|mov|flv)$]").forEach {
                    val href = it.absUrl("href")
                    if (href.isNotEmpty()) {
                        links.add(DetectedLink(
                            title = it.text().takeIf { t -> t.isNotEmpty() } ?: href.substringAfterLast("/"),
                            url = href,
                            type = "VIDEO"
                        ))
                    }
                }

                // Documents
                document.select("a[href~=\\.(pdf|epub|docx|xlsx|zip|rar|7z|tar)$]").forEach {
                    val href = it.absUrl("href")
                    if (href.isNotEmpty()) {
                        links.add(DetectedLink(
                            title = it.text().takeIf { t -> t.isNotEmpty() } ?: href.substringAfterLast("/"),
                            url = href,
                            type = "FILE"
                        ))
                    }
                }

                // HTML5 video sources
                document.select("video source[src]").forEach {
                    val src = it.absUrl("src")
                    if (src.isNotEmpty()) {
                        links.add(DetectedLink(
                            title = src.substringAfterLast("/"),
                            url = src,
                            type = "VIDEO"
                        ))
                    }
                }

                // HTML5 audio sources
                document.select("audio source[src]").forEach {
                    val src = it.absUrl("src")
                    if (src.isNotEmpty()) {
                        links.add(DetectedLink(
                            title = src.substringAfterLast("/"),
                            url = src,
                            type = "AUDIO"
                        ))
                    }
                }

                _detectedLinks.value = links.distinctBy { it.url }.take(50) // Limit to 50 links
            } catch (e: Exception) {
                _detectedLinks.value = emptyList()
            }
        }
    }
}
