package com.example.meditrack3.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.meditrack3.ui.screens.home.HomeScreen
import com.example.meditrack3.ui.screens.medication.MedicationAddScreen
import com.example.meditrack3.ui.screens.medication.MedicationScreen
import com.example.meditrack3.ui.screens.insights.InsightsScreen

@Composable
fun BuildNavigationGraph() {

    val navController = rememberNavController()

    BaseContainer(navHostController = navController) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Home.route) {
                HomeScreen(navController)
            }

            composable(Screen.Medication.route) {
                MedicationScreen(navController)
            }

            composable(Screen.MedicationAdd.route) {
                MedicationAddScreen(navController)
            }

//            composable(Screen.Scan.route) {
//                ScanScreen(navController)
//            }

            composable(Screen.Insights.route) {
                InsightsScreen(navController)
            }

            composable(Screen.MedicationEdit.route) { backStackEntry ->
                val medicationId =
                    backStackEntry.arguments?.getString("medicationId")?.toInt() ?: return@composable

                MedicationAddScreen(
                    navController = navController,
                   // medicationId = medicationId
                )
            }

        }
    }
}
