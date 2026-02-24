package com.example.meditrack3.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
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
import com.example.meditrack3.ui.screens.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BuildNavigationGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        /* ───────── AUTH SCREENS (NO BASECONTAINER) ───────── */

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        composable(Screen.Signup.route) {
            SignupScreen(navController)
        }

        /* ───────── MAIN APP (USES BASECONTAINER) ───────── */

        composable(Screen.Home.route) {
            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    HomeScreen(navController)
                }
            }
        }

        composable(Screen.Medication.route) {
            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    MedicationScreen(navController)
                }
            }
        }

        composable(Screen.MedicationLookUp.route) {
            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    MedicationLookupScreen(navController)
                }
            }
        }

        composable(
            route = "medication_add?name={name}&details={details}",
            arguments = listOf(
                navArgument("name") { nullable = true },
                navArgument("details") { nullable = true }
            )
        ) {
            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    MedicationAddScreen(navController)
                }
            }
        }

        composable(Screen.Insights.route) {
            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    InsightsScreen(navController)
                }
            }
        }
        composable(Screen.Settings.route) {
            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    SettingsScreen(navController)
                }
            }
        }

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

            BaseContainer(navHostController = navController) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    EditMedicationScreen(
                        navController = navController,
                        medicationId = medicationId
                    )
                }
            }
        }
    }
}