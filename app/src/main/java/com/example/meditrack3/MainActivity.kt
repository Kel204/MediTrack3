package com.example.meditrack3

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.meditrack3.navigation.BuildNavigationGraph
import com.example.meditrack3.ui.theme.MediTrack3Theme
import com.example.meditrack3.ui.viewmodels.ThemeViewModel

class MainActivity : ComponentActivity() {

    // 👉 holds where we want to navigate
    private var navigationTarget by mutableStateOf<String?>(null)

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 👉 Get navigation info from notification
        navigationTarget = intent?.getStringExtra("navigate_to")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        enableEdgeToEdge()

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()

            val navController = rememberNavController()

            MediTrack3Theme(
                darkTheme = themeViewModel.isDarkMode.value
            ) {

                // 👉 Handle navigation trigger
                LaunchedEffect(navigationTarget) {
                    navigationTarget?.let { target ->

                        if (target == "home") {
                            navController.navigate("home") {
                                popUpTo(0)
                            }
                        }

                        // reset after handling
                        navigationTarget = null
                    }
                }

                // 👉 IMPORTANT: pass navController
                BuildNavigationGraph( navController = navController)
            }
        }
    }

    // 👉 Handles when app already open
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        navigationTarget = intent?.getStringExtra("navigate_to")
    }
}