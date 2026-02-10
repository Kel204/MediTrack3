package com.example.meditrack3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.meditrack3.data.repository.MedicationRepository

class LoginViewModelFactory(
    private val medicationRepository: MedicationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(medicationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
