package com.example.modularcomposecleanarchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.feature.users.presentation.UsersScreen
import com.example.modularcomposecleanarchitecture.ui.theme.ModularComposeCleanArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity
 * Entry point for the application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModularComposeCleanArchitectureTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Display Users Screen
                    UsersScreen()
                }
            }
        }
    }
}
