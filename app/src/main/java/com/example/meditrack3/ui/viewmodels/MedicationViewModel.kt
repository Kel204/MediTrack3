package com.example.meditrack3.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meditrack3.data.datastore.DoseStatusStore
import com.example.meditrack3.data.entity.Medication
import com.example.meditrack3.data.repository.MedicationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MedicationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MedicationRepository(application)

    val medications: StateFlow<List<Medication>> =
        repository.getAllMedications()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    /* ───────── Dose status persistence (HomeScreen) ───────── */

    // key format: yyyy-MM-dd-medId-time
    private val doseStatusMap = mutableStateMapOf<String, Boolean?>()

    init {
        // Load persisted dose status on app start
        viewModelScope.launch {
            val storedStatuses = DoseStatusStore.loadAll(getApplication())
            storedStatuses.forEach { (key, value) ->
                doseStatusMap[key] = value
            }
        }
    }

    fun getDoseStatus(key: String): Boolean? =
        doseStatusMap[key]

    fun setDoseStatus(key: String, taken: Boolean) {
        doseStatusMap[key] = taken

        // Persist to DataStore
        viewModelScope.launch {
            DoseStatusStore.save(getApplication(), key, taken)
        }
    }

    /* ───────── CRUD ───────── */

    fun loadMedication(id: Int, onLoaded: (Medication?) -> Unit) {
        viewModelScope.launch {
            onLoaded(repository.getMedicationById(id))
        }
    }

    fun addMedication(medication: Medication) {
        viewModelScope.launch {
            repository.insertMedication(medication)
        }
    }

    fun updateMedication(medication: Medication) {
        viewModelScope.launch {
            repository.updateMedication(medication)
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch {
            repository.deleteMedication(medication)
        }
    }

    /* ───────── Auto stock deduction ───────── */

    fun markDoseTaken(medicationId: Int) {
        viewModelScope.launch {
            val med = repository.getMedicationById(medicationId) ?: return@launch

            val newRemaining = (med.remainingQuantity - med.dosePerIntake)
                .coerceAtLeast(0)

            repository.updateMedication(
                med.copy(remainingQuantity = newRemaining)
            )
        }
    }
}
