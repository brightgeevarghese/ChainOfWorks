package com.example.wmchain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wmchain.feature.image.screen.ImageScreen
import com.example.wmchain.ui.theme.WmchainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WmchainTheme {
                ImageScreen()
            }
        }
    }
}
