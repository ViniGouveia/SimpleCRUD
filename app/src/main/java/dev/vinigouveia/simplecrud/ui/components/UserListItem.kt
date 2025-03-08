package dev.vinigouveia.simplecrud.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dev.vinigouveia.simplecrud.R
import dev.vinigouveia.simplecrud.model.User

@Composable
fun UserListItem(
    modifier: Modifier = Modifier,
    user: User,
    onUserClick: (User) -> Unit
) {
    var showProgressBar by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onUserClick(user) }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box {
                if (showProgressBar)
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.imageUrl)
                        .crossfade(true)
                        .listener(
                            onStart = { showProgressBar = true },
                            onSuccess = { _, _ -> showProgressBar = false },
                            onError = { _, _ -> showProgressBar = false }
                        )
                        .error(R.drawable.user_image_placeholder)
                        .build(),
                    contentDescription = "User image",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }

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
}
