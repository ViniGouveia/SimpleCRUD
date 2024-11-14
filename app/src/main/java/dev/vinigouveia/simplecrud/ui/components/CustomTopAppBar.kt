package dev.vinigouveia.simplecrud.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    navigationButton: @Composable (() -> Unit)? = null,
    actionIcon: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            navigationButton?.let { it() }
        },
        actions = {
            actionIcon?.let { it() }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun BackIcon(
    onBackClick: () -> Unit
) {
    IconButton(onClick = { onBackClick() }) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null
        )
    }
}

@Composable
fun SignOutIcon(
    onBackClick: () -> Unit
) {
    IconButton(onClick = { onBackClick() }) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ExitToApp,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null
        )
    }
}
