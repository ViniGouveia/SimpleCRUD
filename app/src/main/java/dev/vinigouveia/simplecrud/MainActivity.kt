package dev.vinigouveia.simplecrud

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val storageReference = firebaseStorage.reference

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var imageUri: MutableState<Uri?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        requestMediaPermission()
        registerActivityForResult()

        enableEdgeToEdge()
        setContent {
            SimpleCRUDTheme {
                imageUri = rememberSaveable { mutableStateOf(null) }

                NavigationBuilder(
                    activity = this,
                    userImage = imageUri.value,
                    authInstance = auth,
                    storageReference = storageReference,
                    dbReference = dbReference,
                    onUserImageClick = { getUserImage() },
                    clearUserImage = { clearImage() }
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    private fun clearImage() {
        imageUri.value = null
    }

    private fun getUserImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activityResultLauncher.launch(intent)
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
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    0
                )
            }
        }
    }

    private fun requestMediaPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                1
            )
        }
    }

    private fun registerActivityForResult() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val imageData = result.data
                    if (resultCode == RESULT_OK && imageData != null) {
                        imageUri.value = imageData.data
                    }
                }
            )
    }

    private companion object {
        const val DB_CHILD = "Users"
    }
}

@Composable
fun NavigationBuilder(
    activity: Activity,
    userImage: Uri?,
    authInstance: FirebaseAuth,
    storageReference: StorageReference,
    dbReference: DatabaseReference,
    onUserImageClick: () -> Unit,
    clearUserImage: () -> Unit
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
                storageReference = storageReference,
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
                storageReference = storageReference,
                dbReference = dbReference,
                userImage = userImage,
                onUserImageClick = { onUserImageClick() }
            ) {
                clearUserImage()
                navController.popBackStack()
            }
        }
        composable(
            route = "updateUser/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            UpdateUserScreen(
                storageReference = storageReference,
                dbReference = dbReference,
                onUserImageClick = { onUserImageClick() },
                userImage = userImage,
                userId = backStackEntry.arguments?.getString("userId") ?: ""
            ) {
                clearUserImage()
                navController.popBackStack()
            }
        }
    }
}
