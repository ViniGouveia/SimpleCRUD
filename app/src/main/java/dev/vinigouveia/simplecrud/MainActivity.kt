package dev.vinigouveia.simplecrud

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dev.vinigouveia.simplecrud.ui.screens.AddUserScreen
import dev.vinigouveia.simplecrud.ui.screens.ForgetPasswordScreen
import dev.vinigouveia.simplecrud.ui.screens.HomeScreen
import dev.vinigouveia.simplecrud.ui.screens.LoginScreen
import dev.vinigouveia.simplecrud.ui.screens.SignInWithPhoneNumberScreen
import dev.vinigouveia.simplecrud.ui.screens.SignUpScreen
import dev.vinigouveia.simplecrud.ui.screens.UpdateUserScreen
import dev.vinigouveia.simplecrud.ui.theme.SimpleCRUDTheme

class MainActivity : ComponentActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val dbReference = database.getReference(DB_CHILD)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            SimpleCRUDTheme {
                NavigationBuilder(this, auth, dbReference)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }

    private companion object {
        const val DB_CHILD = "Users"
    }
}

@Composable
fun NavigationBuilder(
    activity: Activity,
    authInstance: FirebaseAuth,
    dbReference: DatabaseReference
) {
    val navController = rememberNavController()

    val startDestination = authInstance.currentUser?.let { "home" } ?: run { "login" }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                authInstance = authInstance,
                onLoginSuccess = { navController.navigate("home") },
                onSignUpClick = { navController.navigate("signUp") },
                onForgetPasswordClick = { navController.navigate("forgetPassword") },
                onSignInWithPhoneNumberClick = { navController.navigate("signInWithPhoneNumber") }
            )
        }
        composable("signInWithPhoneNumber") {
            SignInWithPhoneNumberScreen(
                activity = activity,
                authInstance = authInstance,
                onSignInSuccess = { navController.navigate("home") },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("signUp") {
            SignUpScreen(
                authInstance = authInstance,
                onSignUpSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("forgetPassword") {
            ForgetPasswordScreen(
                authInstance = authInstance,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen(
                authInstance = authInstance,
                dbReference = dbReference,
                addCallback = { navController.navigate("addUser") },
                updateCallback = { userId ->
                    navController.navigate(route = "updateUser/$userId")
                },
                onBackClick = {
                    if (startDestination == "home") navController.navigate("login")
                    else navController.popBackStack()
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
