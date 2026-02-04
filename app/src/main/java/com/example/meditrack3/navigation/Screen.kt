package com.example.meditrack3.navigation


sealed class Screen(val route: String) {
 object Login : Screen("login")
 object Home : Screen("home")
 object Medication : Screen("medication")
 object MedicationAdd : Screen("medication_add")
 object MedicationEdit : Screen("medication_edit/{medicationId}") {
  fun createRoute(medicationId: Int) =
   "medication_edit/$medicationId"
 }
 object Scan : Screen("scan")
 object Signup : Screen("signup")
 object Insights : Screen("insights")
}

val screens = listOf(
 Screen.Home,
 Screen.Medication,
 Screen.MedicationAdd,
 Screen.Scan,
 Screen.Insights,
 Screen.Login,
 Screen.Signup
)