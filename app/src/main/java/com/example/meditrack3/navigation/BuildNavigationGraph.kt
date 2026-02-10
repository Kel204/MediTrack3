package com.example.meditrack3.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.meditrack3.ui.screens.home.HomeScreen
import com.example.meditrack3.ui.screens.medication.MedicationAddScreen
import com.example.meditrack3.ui.screens.medication.MedicationScreen
import com.example.meditrack3.ui.screens.medication.EditMedicationScreen
import com.example.meditrack3.ui.screens.insights.InsightsScreen
import com.example.meditrack3.ui.screens.login.LoginScreen
import com.example.meditrack3.ui.screens.login.SignupScreen
import com.example.meditrack3.ui.screens.lookup.MedicationLookupScreen

@RequiresApi(Build.VERSION_CODES.O)
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

            composable(Screen.MedicationLookUp.route) {
                MedicationLookupScreen(navController)
            }

            composable(
                route = "medication_add?name={name}&details={details}",
                arguments = listOf(
                    navArgument("name") { nullable = true },
                    navArgument("details") { nullable = true }
                )
            ) {
                MedicationAddScreen(navController)
            }

            composable(Screen.Insights.route) {
                InsightsScreen(navController)
            }

            composable(Screen.Login.route) {
                LoginScreen(navController)
            }

            composable(Screen.Signup.route) {
                SignupScreen(navController)
            }

            // ✅ EDIT MEDICATION (CORRECT & ONLY ONCE)
            composable(
                route = Screen.MedicationEdit.route,
                arguments = listOf(
                    navArgument("medicationId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val medicationId =
                    backStackEntry.arguments?.getInt("medicationId")
                        ?: return@composable

                EditMedicationScreen(
                    navController = navController,
                    medicationId = medicationId
                )
            }
        }
    }
}
