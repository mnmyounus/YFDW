package com.mnmyounus.yfdw.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mnmyounus.yfdw.domain.model.SecurityState
import com.mnmyounus.yfdw.presentation.browser.BrowserScreen
import com.mnmyounus.yfdw.presentation.downloads.DownloadListViewModel
import com.mnmyounus.yfdw.presentation.downloads.DownloadListScreen
import com.mnmyounus.yfdw.presentation.gate.InsecureExitDialog
import com.mnmyounus.yfdw.presentation.gate.ModePickerScreen
import com.mnmyounus.yfdw.presentation.gate.SecurityGateViewModel
import com.mnmyounus.yfdw.presentation.settings.SettingsScreen

object Destinations {
    const val MODE_PICKER = "mode_picker"
    const val BROWSER = "browser"
    const val SETTINGS = "settings"
}

@Composable
fun YfdwNavGraph() {
    val navController = rememberNavController()
    val gateViewModel: SecurityGateViewModel = hiltViewModel()
    val downloadViewModel: DownloadListViewModel = hiltViewModel()
    
    val loaded by gateViewModel.modeLoaded.collectAsState()
    val mode by gateViewModel.mode.collectAsState()
    val securityState by gateViewModel.securityState.collectAsState()

    if (!loaded) return

    NavHost(
        navController = navController,
        startDestination = if (mode == null) Destinations.MODE_PICKER else Destinations.BROWSER
    ) {
        composable(Destinations.MODE_PICKER) {
            ModePickerScreen(onModeChosen = { chosen ->
                gateViewModel.setMode(chosen)
                navController.navigate(Destinations.BROWSER) {
                    popUpTo(Destinations.MODE_PICKER) { inclusive = true }
                }
            })
        }
        
        composable(Destinations.BROWSER) {
            BrowserScreen(onDownloadQueued = { url, name ->
                downloadViewModel.addDownload(url, name)
            })
        }
        
        composable(Destinations.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }

    if (securityState is SecurityState.Insecure) {
        InsecureExitDialog()
    }
}
