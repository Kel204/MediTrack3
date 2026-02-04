package com.example.meditrack3.auth

import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
    }
}
