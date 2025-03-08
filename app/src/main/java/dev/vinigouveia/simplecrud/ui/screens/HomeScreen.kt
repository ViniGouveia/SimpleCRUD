package dev.vinigouveia.simplecrud.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import dev.vinigouveia.simplecrud.model.User
import dev.vinigouveia.simplecrud.ui.components.CustomTopAppBar
import dev.vinigouveia.simplecrud.ui.components.DeleteAllUsersDialog
import dev.vinigouveia.simplecrud.ui.components.LazyColumnWithSwipe
import dev.vinigouveia.simplecrud.ui.components.SignOutIcon
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    authInstance: FirebaseAuth,
    storageReference: StorageReference,
    dbReference: DatabaseReference,
    addCallback: () -> Unit,
    updateCallback: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var usersList = remember { mutableStateOf<List<User>>(listOf()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(
                title = "SimpleCRUD",
                actionIcon = {
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            contentDescription = null
                        )
                    }
                },
                navigationButton = {
                    SignOutIcon { showLogOutDialog = true }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                onClick = {
                    addCallback()
                }
            ) { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddings ->

        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList.value = snapshot.children.map {
                    User(
                        id = it.child("id").value.toString(),
                        name = it.child("name").value.toString(),
                        age = it.child("age").value.toString().toInt(),
                        email = it.child("email").value.toString(),
                        imageUrl = it.child("imageUrl").value.toString()
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Error: ${error.message}",
                        actionLabel = "Dismiss",
                        withDismissAction = true
                    )
                }
            }
        })

        UserList(
            modifier = modifier.padding(paddings),
            usersList = usersList.value,
            onUserClick = {
                updateCallback(it)
            },
            onSwipeItem = {
                scope.launch {
                    dbReference.child(it).removeValue()
                    storageReference.child("images").child(it).delete()
                    snackbarHostState.showSnackbar(
                        message = "User deleted"
                    )
                }
            }
        )

        if (showDeleteDialog) {
            DeleteAllUsersDialog(
                title = "Delete all users",
                text = "This action will delete all users, if you want to remove just one user, swipe it to the left or right. Do you want to continue?",
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    scope.launch {
                        dbReference.removeValue()
                        storageReference.child("images").listAll().addOnCompleteListener {
                            it.result.items.forEach {
                                it.delete()
                            }
                        }
                        snackbarHostState.showSnackbar(
                            message = "All users deleted"
                        )
                    }
                    showDeleteDialog = false
                }
            )
        }

        if (showLogOutDialog) {
            DeleteAllUsersDialog(
                title = "Sign Out",
                text = "Are you sure you want to sign out?",
                onDismiss = { showLogOutDialog = false },
                onConfirm = {
                    showLogOutDialog = false
                    authInstance.signOut()
                    onBackClick()
                }
            )
        }
    }
}

@Composable
fun UserList(
    modifier: Modifier = Modifier,
    usersList: List<User>,
    onUserClick: (String) -> Unit,
    onSwipeItem: (String) -> Unit
) {
    LazyColumnWithSwipe(
        modifier = modifier,
        items = usersList,
        onUserClick = { onUserClick(it) },
        onSwipe = { onSwipeItem(it) }
    )
}
