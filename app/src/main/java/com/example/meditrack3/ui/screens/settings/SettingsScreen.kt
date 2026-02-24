package com.example.meditrack3.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user == null) {
        Text("User not logged in")
        return
    }

    var displayName by remember { mutableStateOf(user.displayName ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val originalEmail = user.email ?: ""

    val createdAt = user.metadata?.creationTimestamp?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(it))
    } ?: "Unknown"

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        ElevatedCard(
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp)
                    )
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current Password (required if changing email)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Account Created: $createdAt",
                    style = MaterialTheme.typography.bodySmall
                )

                Button(
                    onClick = {

                        if (displayName.isBlank()) {
                            Toast.makeText(
                                context,
                                "Name cannot be empty",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        isLoading = true

                        // 1️⃣ Update display name
                        val profileUpdates =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(displayName)
                                .build()

                        user.updateProfile(profileUpdates)

                        // If email didn’t change
                        if (email == originalEmail) {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        // Require password if changing email
                        if (currentPassword.isBlank()) {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Enter your current password to change email",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        val credential =
                            EmailAuthProvider.getCredential(
                                originalEmail,
                                currentPassword
                            )

                        // 2️⃣ Reauthenticate
                        user.reauthenticate(credential)
                            .addOnSuccessListener {

                                // 3️⃣ Direct Email Update (no verification)
                                user.updateEmail(email)
                                    .addOnSuccessListener {

                                        user.reload()

                                        isLoading = false
                                        currentPassword = ""

                                        Toast.makeText(
                                            context,
                                            "Email updated successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "Update failed: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "Wrong password",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Save Changes")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(Icons.Outlined.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out")
        }
    }
}