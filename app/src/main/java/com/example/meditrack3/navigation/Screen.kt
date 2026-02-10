package com.example.meditrack3.navigation


sealed class Screen(val route: String) {
 object Login : Screen("login")
 object Home : Screen("home")
 object Medication : Screen("medication")
 object MedicationAdd : Screen("medication_add")
 object MedicationEdit : Screen("medication_edit/{medicationId}") {
  fun createRoute(medicationId: Int): String =
   "medication_edit/$medicationId"
 }
 object MedicationLookUp : Screen("medication_lookup")
 object Signup : Screen("signup")
 object Insights : Screen("insights")
}

val screens = listOf(
 Screen.Home,
 Screen.Medication,
 Screen.MedicationAdd,
 Screen.MedicationLookUp,
 Screen.Insights,
 Screen.Login,
 Screen.Signup
)