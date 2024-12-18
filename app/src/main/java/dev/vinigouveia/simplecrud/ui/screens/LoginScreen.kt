package dev.vinigouveia.simplecrud.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import dev.vinigouveia.simplecrud.ui.components.CustomTopAppBar
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authInstance: FirebaseAuth,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgetPasswordClick: () -> Unit,
    onSignInWithPhoneNumberClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CustomTopAppBar(title = "Login") },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome!",
                modifier = Modifier.padding(vertical = 50.dp),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            )

            OutlinedTextField(
                value = password,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, bottom = 4.dp, top = 8.dp)
            )

            Text(
                text = "Forget password?",
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clickable { onForgetPasswordClick() },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 8.dp, top = 32.dp),
                onClick = {
                    authInstance.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${it.exception?.message}",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    }
                }
            ) {
                Text("Sign In")
            }

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 8.dp),
                onClick = { onSignUpClick() }
            ) { Text("Sign Up") }

            Button(
                modifier = Modifier.width(200.dp),
                onClick = { onSignInWithPhoneNumberClick() }
            ) { Text("Sign in using phone number", textAlign = TextAlign.Center) }
        }
    }
}
