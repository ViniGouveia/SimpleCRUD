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
import java.util.UUID

@Composable
fun AddUserScreen(
    storageReference: StorageReference,
    dbReference: DatabaseReference,
    onUserImageClick: () -> Unit,
    userImage: Uri?,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(0) }
    var showProgressBar by remember { mutableStateOf(false) }
    var showAddProgressBar by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(
                title = "Add New User",
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
                if (showProgressBar)
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userImage)
                        .crossfade(true)
                        .error(R.drawable.user_image_placeholder)
                        .placeholder(R.drawable.user_image_placeholder)
                        .listener(
                            onStart = {
                                showProgressBar = true
                            },
                            onSuccess = { _, _ ->
                                showProgressBar = false
                            },
                            onError = { _, _ ->
                                showProgressBar = false
                            }
                        )
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
                    val id = dbReference.push().key ?: UUID.randomUUID().toString()
                    val user = User(
                        id = id,
                        name = name,
                        email = email,
                        age = age,
                        imageUrl = userImage.toString()
                    )

                    showAddProgressBar = true

                    userImage?.let {
                        storageReference.child("images").child(id).putFile(it)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    storageReference.child("images")
                                        .child(id).downloadUrl.addOnSuccessListener {
                                            dbReference.child(id).setValue(user)
                                                .addOnCompleteListener { task ->
                                                    showAddProgressBar = false

                                                    if (task.isSuccessful) {
                                                        scope.launch {
                                                            onBackClick()
                                                            snackbarHostState.showSnackbar("User added successfully")
                                                        }
                                                    } else {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Error adding user")
                                                        }
                                                    }
                                                }
                                        }
                                } else {
                                    showAddProgressBar = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error adding user")
                                    }
                                }
                            }
                    }.run {
                        dbReference.child(id).setValue(user)
                            .addOnCompleteListener { task ->
                                showAddProgressBar = false

                                if (task.isSuccessful) {
                                    scope.launch {
                                        onBackClick()
                                        snackbarHostState.showSnackbar("User added successfully")
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error adding user")
                                    }
                                }
                            }
                    }
                }
            ) {
                Text("Save")
            }

            if (showAddProgressBar)
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
