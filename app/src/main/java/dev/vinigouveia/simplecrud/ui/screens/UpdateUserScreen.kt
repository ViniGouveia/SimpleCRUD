package dev.vinigouveia.simplecrud.ui.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dev.vinigouveia.simplecrud.R
import dev.vinigouveia.simplecrud.model.User
import dev.vinigouveia.simplecrud.ui.components.BackIcon
import dev.vinigouveia.simplecrud.ui.components.CustomTopAppBar
import kotlinx.coroutines.launch

@Composable
fun UpdateUserScreen(
    storageReference: StorageReference,
    dbReference: DatabaseReference,
    userId: String,
    onUserImageClick: () -> Unit,
    userImage: Uri?,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(0) }
    var imageUrl by remember { mutableStateOf("") }
    var showImageProgressBar by remember { mutableStateOf(false) }
    var showUpdateProgressBar by remember { mutableStateOf(false) }

    dbReference.child(userId).get().addOnCompleteListener {
        if (it.isSuccessful) {
            val result = it.result.getValue(User::class.java)
            if (result != null) {
                name = result.name
                email = result.email
                age = result.age
                imageUrl = userImage?.toString() ?: result.imageUrl
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(
                title = "Update User",
                navigationButton = {
                    BackIcon(onBackClick = onBackClick)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box {
                if (showImageProgressBar)
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .listener(
                            onStart = { showImageProgressBar = true },
                            onSuccess = { _, _ ->
                                showImageProgressBar = false
                            },
                            onError = { _, _ ->
                                showImageProgressBar = false
                            }
                        )
                        .error(R.drawable.user_image_placeholder)
                        .build(),
                    contentDescription = "User image",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(100.dp)
                        .clickable { onUserImageClick() },
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = age.toString(),
                onValueChange = { age = it.toIntOrNull() ?: 0 },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    showUpdateProgressBar = true

                    userImage?.let {
                        storageReference.child("images").child(userId).putFile(it)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    storageReference.child("images")
                                        .child(userId).downloadUrl.addOnSuccessListener {
                                            dbReference.child(userId).updateChildren(
                                                mapOf(
                                                    "name" to name,
                                                    "email" to email,
                                                    "age" to age,
                                                    "imageUrl" to it.toString()
                                                )
                                            ).addOnCompleteListener {
                                                showUpdateProgressBar = false

                                                if (it.isSuccessful) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("User updated successfully")
                                                    }
                                                    onBackClick()
                                                } else {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Error updating user")
                                                    }
                                                }
                                            }
                                        }
                                } else {
                                    showUpdateProgressBar = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error updating image")
                                    }
                                }
                            }
                    }
                }
            ) {
                Text("Save")
            }

            if (showUpdateProgressBar)
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
