package dev.vinigouveia.simplecrud.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.vinigouveia.simplecrud.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LazyColumnWithSwipe(
    modifier: Modifier = Modifier,
    items: List<User>,
    onSwipe: (String) -> Unit,
    onUserClick: (String) -> Unit
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(12.dp)) {
        items(items, key = { it.id }) { user ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    when (it) {
                        SwipeToDismissBoxValue.EndToStart,
                        SwipeToDismissBoxValue.StartToEnd -> {
                            onSwipe(user.id)
                            true
                        }

                        else -> false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {}
            ) {
                UserListItem(user = user) {
                    onUserClick(user.id)
                }
            }
        }
    }
}
