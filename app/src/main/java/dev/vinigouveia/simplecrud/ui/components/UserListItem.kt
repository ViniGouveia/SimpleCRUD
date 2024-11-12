package dev.vinigouveia.simplecrud.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.vinigouveia.simplecrud.model.User

@Composable
fun UserListItem(
    modifier: Modifier = Modifier,
    user: User,
    onUserClick: (User) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onUserClick(user) }
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = modifier.padding(bottom = 8.dp),
                text = user.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                modifier = modifier.padding(bottom = 8.dp),
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = user.age.toString(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
