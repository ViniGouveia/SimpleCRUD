package dev.vinigouveia.simplecrud.ui.screens

import android.app.Activity
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dev.vinigouveia.simplecrud.ui.components.BackIcon
import dev.vinigouveia.simplecrud.ui.components.CustomTopAppBar
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun SignInWithPhoneNumberScreen(
    activity: Activity,
    authInstance: FirebaseAuth,
    onSignInSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var phoneNumber by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }

    var verificationCode by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopAppBar(
                title = "Sign in",
                navigationButton = {
                    BackIcon(onBackClick = onBackClick)
                }
            )
        },
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
                text = "Sign in with your phone number",
                modifier = Modifier.padding(vertical = 38.dp),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Enter your phone number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp)
            )

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 24.dp),
                onClick = {
                    val options = PhoneAuthOptions.newBuilder(authInstance)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(
                            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                                }

                                override fun onVerificationFailed(p0: FirebaseException) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Error: ${p0.message}",
                                            withDismissAction = true
                                        )
                                    }
                                }

                                override fun onCodeSent(
                                    p0: String,
                                    p1: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    super.onCodeSent(p0, p1)
                                    verificationCode = p0
                                }
                            }
                        )
                        .build()

                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            ) {
                Text("Send SMS code")
            }

            OutlinedTextField(
                value = passcode,
                onValueChange = { passcode = it },
                label = { Text("Enter the code sent to your phone") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )

            Button(
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 8.dp),
                onClick = {
                    val credential = PhoneAuthProvider.getCredential(verificationCode, passcode)

                    authInstance.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            onSignInSuccess()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Sign in successful",
                                    withDismissAction = true
                                )
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${it.exception?.message}",
                                    withDismissAction = true
                                )
                            }
                        }
                    }

                }
            ) {
                Text("Verify code to sign in")
            }
        }
    }
}
