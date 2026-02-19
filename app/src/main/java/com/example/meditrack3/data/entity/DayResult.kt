package com.example.meditrack3.data.entity

import androidx.compose.ui.graphics.Color

data class DayResult(
    val totalScheduled: Int,
    val totalTaken: Int,
    val adherence: Int,
    val color: Color,
    val missedMeds: List<String>
)