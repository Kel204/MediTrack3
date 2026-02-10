package com.example.meditrack3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meditrack3.data.repository.MedicationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val medicationRepository: MedicationRepository
) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /* ───────── Login ───────── */

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // ✅ Clear Room data on successful login
                viewModelScope.launch {
                    medicationRepository.clearLocalData()
                    medicationRepository.restoreFromFirebase()
                    medicationRepository.syncAllToFirebase()
                    _loginState.value = LoginState.Success
                }
            }
            .addOnFailureListener { exception ->
                _loginState.value = LoginState.Error(
                    exception.message ?: "Login failed"
                )
            }
    }

    /* ───────── Sign up ───────── */

    private val _signupState = MutableStateFlow<SignupState>(SignupState.Idle)
    val signupState: StateFlow<SignupState> = _signupState

    fun signUp(email: String, password: String) {
        _signupState.value = SignupState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                // ✅ Clear Room data for new user as well
                viewModelScope.launch {
                    medicationRepository.clearLocalData()
                    medicationRepository.restoreFromFirebase()
                    _signupState.value = SignupState.Success(
                        userId = result.user?.uid ?: ""
                    )
                }
            }
            .addOnFailureListener { exception ->
                _signupState.value = SignupState.Error(
                    exception.message ?: "Sign up failed"
                )
            }
    }
}

/* ───────── UI States ───────── */

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    data class Success(val userId: String) : SignupState()
    data class Error(val message: String) : SignupState()
}
