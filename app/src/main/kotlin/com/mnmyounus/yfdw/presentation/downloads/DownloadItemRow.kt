package com.mnmyounus.yfdw.presentation.downloads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mnmyounus.yfdw.domain.model.DownloadItem
import com.mnmyounus.yfdw.domain.model.DownloadStatus

@Composable
fun DownloadItemRow(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    ListItem(
        headlineContent = { Text(item.fileName) },
        supportingContent = {
            Column {
                val progress = if (item.totalBytes > 0) item.downloadedBytes.toFloat() / item.totalBytes else 0f
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                Text(item.status.name)
            }
        },
        trailingContent = {
            Row {
                when (item.status) {
                    DownloadStatus.RUNNING -> TextButton(onClick = onPause) { Text("Pause") }
                    DownloadStatus.PAUSED, DownloadStatus.QUEUED -> TextButton(onClick = onResume) { Text("Resume") }
                    else -> {}
                }
                TextButton(onClick = onCancel) { Text("Cancel") }
            }
        }
    )
}
