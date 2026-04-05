package com.example.meditrack3.navigation.drawer

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.meditrack3.auth.AuthManager
import com.example.meditrack3.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    val user = AuthManager.currentUser.value
    val isLoggedIn = user != null

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val firebaseUser = auth.currentUser

    var profileImageBase64 by remember { mutableStateOf<String?>(null) }

    //Load profile image
    LaunchedEffect(firebaseUser) {
        firebaseUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener {
                    profileImageBase64 = it.getString("profileImage")
                }
        }
    }

    //Decode image from firebase
    val bitmap = profileImageBase64?.let {
        val bytes = Base64.decode(it, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        /* ───────── Header ───────── */

        Row(verticalAlignment = Alignment.CenterVertically) {

            // update profile image
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = if (isLoggedIn) "Welcome back," else "Welcome",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (isLoggedIn) AuthManager.getUserName() else "MediTrack",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }


        /* ───────── Navigation Section ───────── */

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            if (!isLoggedIn) {
                DrawerItem(
                    title = "Log in",
                    icon = Icons.Filled.Login,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            }

            DrawerItem("Home", Icons.Filled.Home) {
                navigateAndClose(navController, drawerState, scope, Screen.Home.route)
            }

            DrawerItem("Medications", Icons.Filled.Medication) {
                navigateAndClose(navController, drawerState, scope, Screen.Medication.route)
            }

            DrawerItem("Add Medication", Icons.Filled.AddCircle) {
                navigateAndClose(navController, drawerState, scope, "medication_add")
            }

            DrawerItem("Medication Lookup", Icons.Filled.Search) {
                navigateAndClose(navController, drawerState, scope, "medication_lookup")
            }

            DrawerItem("Insights", Icons.Filled.Assessment) {
                navigateAndClose(navController, drawerState, scope, Screen.Insights.route)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        /* ───────── Bottom Section ───────── */

        if (isLoggedIn) {
            DrawerItem(
                title = "Log out",
                icon = Icons.Filled.Logout,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                scope.launch { drawerState.close() }
                AuthManager.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
        }
    }
}

/* ───────── Drawer Item ───────── */

@Composable
private fun DrawerItem(
    title: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/* ───────── Navigation Helper ───────── */

private fun navigateAndClose(
    navController: NavHostController,
    drawerState: DrawerState,
    scope: kotlinx.coroutines.CoroutineScope,
    route: String
) {
    scope.launch {
        drawerState.close()
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}