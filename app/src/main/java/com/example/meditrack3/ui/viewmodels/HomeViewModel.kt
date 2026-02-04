package com.example.meditrack3.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meditrack3.data.entity.Medication
import com.example.meditrack3.data.repository.MedicationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MedicationRepository(application)

    /**
     * All medications (shared with Medication screen)
     */
    val medications: StateFlow<List<Medication>> =
        repository.getAllMedications()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    /**
     * Simple summary data for Home screen
     */
    val medicationCount: StateFlow<Int> =
        medications
            .map { it.size }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )
}
