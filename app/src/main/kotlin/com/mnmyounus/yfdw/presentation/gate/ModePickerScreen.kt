package com.mnmyounus.yfdw.presentation.gate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mnmyounus.yfdw.domain.model.OperatingMode

@Composable
fun ModePickerScreen(onModeChosen: (OperatingMode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose how YFDW runs", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Privacy & Anonymity", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text("Requires an active VPN or the built-in Tor route. Downloads refuse to run, and the app exits, without one.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { onModeChosen(OperatingMode.PRIVACY_ANONYMITY) }) {
                    Text("Use Privacy & Anonymity mode")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Standard", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text("No VPN/Tor requirement — downloads work on any network. Encryption, scoped storage, and the malware check stay on.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { onModeChosen(OperatingMode.STANDARD_SECURE) }) {
                    Text("Use Standard mode")
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("You can switch modes later in Settings.", style = MaterialTheme.typography.bodySmall)
    }
}
