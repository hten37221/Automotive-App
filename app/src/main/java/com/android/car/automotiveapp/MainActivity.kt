package com.android.car.automotiveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.car.automotiveapp.core.designsystem.theme.AutomotiveTheme
import com.android.car.automotiveapp.feature.home.AutomotiveNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutomotiveTheme {
                AutomotiveNavHost()
            }
        }
    }
}