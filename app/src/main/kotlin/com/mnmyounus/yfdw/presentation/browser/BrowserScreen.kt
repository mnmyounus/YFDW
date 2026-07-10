package com.mnmyounus.yfdw.presentation.browser

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

data class DetectedLink(
    val title: String,
    val url: String,
    val type: String
)

@Composable
fun BrowserScreen(
    onDownloadQueued: (String, String) -> Unit
) {
    val viewModel: BrowserViewModel = hiltViewModel()
    val detectedLinks by viewModel.detectedLinks.collectAsState()
    val currentUrl by viewModel.currentUrl.collectAsState()
    var showLinksList by remember { mutableStateOf(false) }
    var searchInput by remember { mutableStateOf(TextFieldValue("https://example.com")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF081210))
    ) {
        // Address/Search bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search or URL") },
                singleLine = true,
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(999.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF23362F),
                    focusedBorderColor = Color(0xFF2BBE98)
                )
            )
            IconButton(
                onClick = {
                    val urlToLoad = when {
                        searchInput.text.startsWith("http") -> searchInput.text
                        searchInput.text.contains(".") -> "https://${searchInput.text}"
                        else -> "https://www.google.com/search?q=${searchInput.text}"
                    }
                    viewModel.loadUrl(urlToLoad)
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF2BBE98), RoundedCornerShape(50%))
            ) {
                Icon(Icons.Default.Search, "", tint = Color(0xFF081210))
            }
        }

        // WebView Container
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                if (url != null) {
                                    viewModel.extractLinks(url)
                                }
                            }
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            mixedContentMode = WebView.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                    }
                },
                update = { webView ->
                    if (currentUrl.isNotEmpty() && webView.url != currentUrl) {
                        webView.loadUrl(currentUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Floating button showing detected links count
            if (detectedLinks.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showLinksList = !showLinksList },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    containerColor = Color(0xFF2BBE98),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(Icons.Default.Download, "Downloads", tint = Color(0xFF081210), modifier = Modifier.size(20.dp))
                        Text(
                            detectedLinks.size.toString(),
                            fontSize = MaterialTheme.typography.labelSmall.fontSize,
                            color = Color(0xFF081210)
                        )
                    }
                }
            }
        }

        // Bottom sheet with detected links
        if (showLinksList && detectedLinks.isNotEmpty()) {
            DetectedLinksSheet(
                links = detectedLinks,
                onDownload = { link ->
                    onDownloadQueued(link.url, link.title)
                    showLinksList = false
                },
                onDismiss = { showLinksList = false }
            )
        }
    }
}

@Composable
fun DetectedLinksSheet(
    links: List<DetectedLink>,
    onDownload: (DetectedLink) -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2F29))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Detected Files (${links.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Search, "Close")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(links) { link ->
                    LinkItemRow(link, onDownload)
                }
            }
        }
    }
}

@Composable
fun LinkItemRow(
    link: DetectedLink,
    onDownload: (DetectedLink) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142420))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    link.title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    color = Color(0xFFEAF6F1)
                )
                Text(
                    link.type.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7E9C94)
                )
            }
            Button(
                onClick = { onDownload(link) },
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(50%),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2BBE98))
            ) {
                Icon(Icons.Default.Download, "Download", modifier = Modifier.size(18.dp), tint = Color(0xFF081210))
            }
        }
    }
}
