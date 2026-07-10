package com.mnmyounus.yfdw.presentation.gate

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * Cosmetic only — the real kill switch is SecurityGate.killSecurely(),
 * which works whether or not this dialog is ever drawn. This just gives
 * foreground users an explanation in the ~3s before exitProcess(0) fires.
 */
@Composable
fun InsecureExitDialog() {
    AlertDialog(
        onDismissRequest = { /* not dismissible — the process is exiting regardless */ },
        title = { Text("Insecure network detected") },
        text = { Text("No VPN or Tor route was found. YFDW is closing to protect your privacy.") },
        confirmButton = {}
    )
}
