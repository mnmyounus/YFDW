package com.mnmyounus.yfdw.presentation.downloads

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnmyounus.yfdw.domain.model.DownloadItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadListScreen(onOpenSettings: () -> Unit) {
    val viewModel: DownloadListViewModel = hiltViewModel()
    val downloads by viewModel.downloads.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YFDW") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add download")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(downloads, key = { it.id }) { item: DownloadItem ->
                DownloadItemRow(
                    item = item,
                    onPause = { viewModel.pause(item.id) },
                    onResume = { viewModel.resume(item.id) },
                    onCancel = { viewModel.cancel(item.id) }
                )
            }
        }
    }

    if (showAddSheet) {
        AddDownloadSheet(
            onDismiss = { showAddSheet = false },
            onConfirm = { url, name ->
                viewModel.addDownload(url, name)
                showAddSheet = false
            }
        )
    }
}
