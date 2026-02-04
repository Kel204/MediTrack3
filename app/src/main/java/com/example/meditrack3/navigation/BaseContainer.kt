package com.example.meditrack3.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import com.example.meditrack3.navigation.drawer.DrawerContent

@Composable
fun BaseContainer(
    navHostController: NavHostController,
    pageContent: @Composable (innerPadding: PaddingValues) -> Unit = {}
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController = navHostController,
                drawerState = drawerState
            )
        }
    ) {
        Scaffold(
            // 🤍 MAIN CONTENT AREA = WHITE
            containerColor = MaterialTheme.colorScheme.surface,

            // 🌸 TOP BAR = PINK (handled internally)
            topBar = {
                TopBarNavigation(
                    drawerState = drawerState
                )
            },

            // Optional: bottom bar already styled by theme
            bottomBar = {
                BottomNavigationBar(navHostController)
            }
        ) { padding ->
            pageContent(padding)
        }
    }
}
