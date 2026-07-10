package com.mnmyounus.yfdw.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mnmyounus.yfdw.domain.model.OperatingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val mode by viewModel.mode.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Operating mode", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Privacy & Anonymity") },
                supportingContent = { Text("Requires VPN or Tor. Switching this on re-checks your connection immediately.") },
                trailingContent = {
                    RadioButton(
                        selected = mode == OperatingMode.PRIVACY_ANONYMITY,
                        onClick = { viewModel.setMode(OperatingMode.PRIVACY_ANONYMITY) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Standard") },
                supportingContent = { Text("No VPN/Tor requirement. Downloads run on any network.") },
                trailingContent = {
                    RadioButton(
                        selected = mode == OperatingMode.STANDARD_SECURE,
                        onClick = { viewModel.setMode(OperatingMode.STANDARD_SECURE) }
                    )
                }
            )
        }
    }
}
