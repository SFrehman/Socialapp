package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.ChatViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle incoming shared URL on start-up
        handleShareIntent(intent)

        setContent {
            MyApplicationTheme(darkTheme = appViewModel.isDarkTheme) {
                MainAppScreen(
                    appViewModel = appViewModel,
                    chatViewModel = chatViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                // Immediately transition to Home (page 0) and trigger URL processing
                appViewModel.currentScreen = 0
                appViewModel.processUrl(sharedText)
            }
        }
    }
}
