package dev.vinigouveia.simplecrud.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import dev.vinigouveia.simplecrud.model.User
import dev.vinigouveia.simplecrud.ui.components.BackIcon
import dev.vinigouveia.simplecrud.ui.components.CustomTopAppBar
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddUserScreen(
    dbReference: DatabaseReference,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(0) }

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
                        age = age
                    )
                    dbReference.child(id).setValue(user).addOnCompleteListener { task ->
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
            ) {
                Text("Save")
            }
        }
    }
}
