package dev.vinigouveia.simplecrud.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.vinigouveia.simplecrud.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _usersList = MutableStateFlow<List<User>>(listOf())
    val usersList = _usersList.asStateFlow()

    fun getUser(): User {
        val userId = savedStateHandle.get<String>("userId") ?: ""

        return User()
    }

    fun deleteAllUsers() {

    }

    fun deleteUser(userId: String) {

    }

    fun addUser(newUser: User) {

    }

    fun updateUser(user: User) {

    }
}
