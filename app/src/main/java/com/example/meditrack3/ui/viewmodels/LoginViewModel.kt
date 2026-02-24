package com.example.meditrack3.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meditrack3.data.repository.MedicationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Now we can pass application safely
    private val medicationRepository = MedicationRepository(application)

    /* ───────── Login ───────── */

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    medicationRepository.clearLocalData()
                    medicationRepository.restoreFromFirebase()
                    _loginState.value = LoginState.Success
                }
            }
            .addOnFailureListener { exception ->
                _loginState.value =
                    LoginState.Error(exception.message ?: "Login failed")
            }
    }

    /* ───────── Sign up ───────── */

    private val _signupState = MutableStateFlow<SignupState>(SignupState.Idle)
    val signupState: StateFlow<SignupState> = _signupState

    fun signUp(email: String, password: String) {
        _signupState.value = SignupState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userData = hashMapOf(
                    "email" to email,
                    "createdAt" to Timestamp.now()
                )

                firestore.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener {

                        viewModelScope.launch {
                            medicationRepository.clearLocalData()
                            _signupState.value = SignupState.Success(uid)
                        }
                    }
                    .addOnFailureListener { e ->
                        _signupState.value =
                            SignupState.Error(
                                e.message ?: "Failed to create user profile"
                            )
                    }
            }
            .addOnFailureListener { exception ->
                _signupState.value =
                    SignupState.Error(exception.message ?: "Sign up failed")
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
