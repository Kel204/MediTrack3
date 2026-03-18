package com.example.meditrack3.ui.screens.settings

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.meditrack3.ui.viewmodels.ThemeViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream

@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = viewModel() // 🔥 auto provided
) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val user = auth.currentUser ?: return

    var name by remember { mutableStateOf(user.displayName ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var newPassword by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }

    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var emergencyContact by remember { mutableStateOf("") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }

    var profileImageBase64 by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteInput by remember { mutableStateOf("") }
    var deletePassword by remember { mutableStateOf("") }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            selectedImageUri = it
        }

    // LOAD DATA + APPLY THEME
    LaunchedEffect(Unit) {
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener {
                allergies = it.getString("allergies") ?: ""
                conditions = it.getString("conditions") ?: ""
                emergencyContact = it.getString("emergencyContact") ?: ""
                notificationsEnabled = it.getBoolean("notifications") ?: true
                darkMode = it.getBoolean("darkMode") ?: false
                profileImageBase64 = it.getString("profileImage")

                themeViewModel.toggleDarkMode(darkMode)
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // PROFILE IMAGE
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier.size(130.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {

                val bitmap = profileImageBase64?.let {
                    val bytes = Base64.decode(it, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }

                when {
                    selectedImageUri != null -> {
                        val bytes = context.contentResolver.openInputStream(selectedImageUri!!)?.readBytes()
                        bytes?.let {
                            Image(
                                bitmap = BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    bitmap != null -> {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Box(
                            Modifier.size(120.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) { Text("Add Photo") }
                    }
                }

                Box(
                    Modifier.size(36.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Edit, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Text("Tap to change photo", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

        // ACCOUNT
        SettingsCard("Account") {
            SettingsTextField("Name", name) { name = it }
            SettingsTextField("Email", email) { email = it }
            SettingsTextField("New Password", newPassword, true) { newPassword = it }
            SettingsTextField("Current Password", currentPassword, true) { currentPassword = it }
        }

        // MEDICAL
        SettingsCard("Medical Profile") {
            SettingsTextField("Allergies", allergies) { allergies = it }
            SettingsTextField("Conditions", conditions) { conditions = it }
            SettingsTextField("Emergency Contact", emergencyContact) { emergencyContact = it }
        }

        // 🔥 APP SETTINGS (FIXED DARK MODE)
        SettingsCard("App Settings") {
            SettingsToggle("Notifications", notificationsEnabled) { notificationsEnabled = it }

            SettingsToggle("Dark Mode", darkMode) {
                darkMode = it
                themeViewModel.toggleDarkMode(it)

                db.collection("users").document(user.uid)
                    .update("darkMode", it)
            }
        }

        // SAVE
        Button(
            onClick = {
                if (currentPassword.isBlank()) {
                    Toast.makeText(context, "Enter password", Toast.LENGTH_LONG).show()
                    return@Button
                }

                isLoading = true

                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

                user.reauthenticate(credential).addOnSuccessListener {

                    user.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    )

                    selectedImageUri?.let {
                        val bytes = context.contentResolver.openInputStream(it)?.readBytes()
                        bytes?.let {
                            val stream = ByteArrayOutputStream()
                            BitmapFactory.decodeByteArray(it, 0, it.size)
                                .compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, stream)
                            profileImageBase64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                        }
                    }

                    db.collection("users").document(user.uid).set(
                        mapOf(
                            "profileImage" to profileImageBase64,
                            "allergies" to allergies,
                            "conditions" to conditions,
                            "emergencyContact" to emergencyContact,
                            "notifications" to notificationsEnabled,
                            "darkMode" to darkMode
                        ),
                        SetOptions.merge()
                    )

                    isLoading = false
                    Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Save Changes")
        }

        // DANGER ZONE
        SettingsCard("Danger Zone", MaterialTheme.colorScheme.errorContainer) {

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Outlined.Logout, null)
                Text(" Log Out")
            }

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Outlined.Delete, null)
                Text(" Delete Account")
            }
        }
    }

    // DELETE DIALOG
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account", color = MaterialTheme.colorScheme.error) },
            text = {
                Column {
                    Text("Type DELETE and enter password.")

                    OutlinedTextField(deleteInput, { deleteInput = it }, label = { Text("DELETE") })
                    OutlinedTextField(
                        deletePassword,
                        { deletePassword = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    deleteError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = deleteInput == "DELETE" && deletePassword.isNotBlank(),
                    onClick = {
                        val credential = EmailAuthProvider.getCredential(user.email!!, deletePassword)

                        user.reauthenticate(credential)
                            .addOnSuccessListener {
                                user.delete().addOnSuccessListener {
                                    db.collection("users").document(user.uid).delete()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                deleteError = "Incorrect password"
                            }
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}