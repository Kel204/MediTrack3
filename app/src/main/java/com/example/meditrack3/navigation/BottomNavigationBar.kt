package com.example.meditrack3.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.meditrack3.R

@Composable
fun BottomNavigationBar(
    navHostController: NavHostController
) {
    val screens = listOf(
        Screen.Home,
        Screen.Medication,
        Screen.Scan,
        Screen.Insights
    )

    val icons = mapOf(
        Screen.Home to NavBarIcon(
            filledIcon = Icons.Filled.Home,
            outlinedIcon = Icons.Outlined.Home,
            label = stringResource(R.string.nav_home)
        ),
        Screen.Medication to NavBarIcon(
            painter = painterResource(R.drawable.ic_medication),
            label = stringResource(R.string.nav_medication)
        ),
        Screen.Scan to NavBarIcon(
            filledIcon = Icons.Filled.AddCircle,
            outlinedIcon = Icons.Outlined.AddCircle,
            label = stringResource(R.string.nav_scan)
        ),
        Screen.Insights to NavBarIcon(
            filledIcon = Icons.Filled.Check,
            outlinedIcon = Icons.Outlined.Check,
            label = stringResource(R.string.nav_insights)
        )
    )

    NavigationBar {
        val navBackStackEntry by navHostController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val icon = icons[screen]!!

            NavigationBarItem(
                selected = isSelected,
                icon = {
                    when {
                        icon.painter != null -> {
                            Icon(
                                painter = icon.painter,
                                contentDescription = icon.label
                            )
                        }
                        isSelected -> {
                            Icon(
                                imageVector = icon.filledIcon!!,
                                contentDescription = icon.label
                            )
                        }
                        else -> {
                            Icon(
                                imageVector = icon.outlinedIcon!!,
                                contentDescription = icon.label
                            )
                        }
                    }
                },
                label = { Text(icon.label) },
                onClick = {
                    navHostController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}
