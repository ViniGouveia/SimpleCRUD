package dev.vinigouveia.simplecrud.ui.screens

import android.widget.Toast
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.vinigouveia.simplecrud.model.User
import dev.vinigouveia.simplecrud.ui.components.CustomTopAppBar
import dev.vinigouveia.simplecrud.ui.components.DeleteAllUsersDialog
import dev.vinigouveia.simplecrud.ui.components.LazyColumnWithSwipe
import dev.vinigouveia.simplecrud.viewModels.MainViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    addCallback: () -> Unit,
    updateCallback: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteToast by remember { mutableStateOf(false) }
    var showDeleteAllToast by remember { mutableStateOf(false) }

    val usersList by viewModel.usersList.collectAsState()

    if (showDialog) {
        DeleteAllUsersDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                viewModel.deleteAllUsers()
                showDialog = false
                showDeleteAllToast = true
            }
        )
    }

    if (showDeleteAllToast) {
        Toast.makeText(LocalContext.current, "All users deleted", Toast.LENGTH_SHORT).show()
        showDeleteAllToast = false
    }

    if (showDeleteToast) {
        Toast.makeText(LocalContext.current, "User deleted", Toast.LENGTH_SHORT).show()
        showDeleteToast = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(
                title = "SimpleCRUD",
                actionIcon = {
                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.primaryContainer,
                            contentDescription = null
                        )
                    }
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
        floatingActionButtonPosition = FabPosition.End
    ) { paddings ->
        UserList(
            modifier = modifier.padding(paddings),
            usersList = usersList,
            onUserClick = {
                updateCallback(it)
            },
            onSwipeItem = {
                viewModel.deleteUser(it)
                showDeleteToast = true
            }
        )
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
