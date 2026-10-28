package com.example.tareaapis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tareaapis.ui.navigation.AppNavigation
import com.example.tareaapis.ui.theme.TareaApisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TareaApisTheme {
                AppNavigation()
            }
        }
    }
}
