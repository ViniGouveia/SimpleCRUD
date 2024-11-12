package dev.vinigouveia.simplecrud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dev.vinigouveia.simplecrud.ui.screens.AddUserScreen
import dev.vinigouveia.simplecrud.ui.screens.HomeScreen
import dev.vinigouveia.simplecrud.ui.screens.UpdateUserScreen
import dev.vinigouveia.simplecrud.ui.theme.SimpleCRUDTheme

class MainActivity : ComponentActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val dbReference = database.getReference(DB_CHILD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCRUDTheme {
                NavigationBuilder(dbReference)
            }
        }
    }

    private companion object {
        const val DB_CHILD = "Users"
    }
}

@Composable
fun NavigationBuilder(
    dbReference: DatabaseReference
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                dbReference = dbReference,
                addCallback = { navController.navigate("addUser") },
                updateCallback = { userId ->
                    navController.navigate(route = "updateUser/$userId")
                }
            )
        }
        composable("addUser") {
            AddUserScreen(
                dbReference = dbReference,
            ) {
                navController.popBackStack()
            }
        }
        composable(
            route = "updateUser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            UpdateUserScreen(
                dbReference = dbReference,
                backStackEntry.arguments?.getString("userId") ?: ""
            ) {
                navController.popBackStack()
            }
        }
    }
}
