package com.example.meditrack3.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = mutableStateOf<FirebaseUser?>(auth.currentUser)
    val currentUser: State<FirebaseUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun isLoggedIn(): Boolean =
        currentUser.value != null

    fun getUserId(): String? =
        currentUser.value?.uid

    fun getUserName(): String {
        val user = currentUser.value
        return user?.displayName
            ?: user?.email
            ?: "User"
    }

    fun logout() {
        auth.signOut()
    }
}

