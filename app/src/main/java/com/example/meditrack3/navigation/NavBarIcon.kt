package com.example.meditrack3.navigation

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarIcon(
    val filledIcon: ImageVector? = null,
    val outlinedIcon: ImageVector? = null,
    val painter: Painter? = null,
    val label: String
)