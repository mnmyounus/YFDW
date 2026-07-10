package com.mnmyounus.yfdw.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.mnmyounus.yfdw.presentation.navigation.YfdwNavGraph
import com.mnmyounus.yfdw.presentation.theme.YfdwTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YfdwTheme {
                Surface { YfdwNavGraph() }
            }
        }
    }
}
