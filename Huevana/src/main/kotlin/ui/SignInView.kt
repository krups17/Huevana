package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.presentation.SignInViewModel

@Composable
fun SignInView(
    viewModel: SignInViewModel,
    onSignedIn: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F7FB)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(0.55f)
        ) {
            Text(
                text = "Huevana",
                fontSize = 28.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                singleLine = true,
            )

            viewModel.errorMessage.value?.let { msg ->
                Text(msg, color = Color(0xFFD32F2F), modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.signIn()) onSignedIn()
                    }
                },
                enabled = !viewModel.isSigningIn.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE95D7A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Text("Sign In")
            }

            Text(
                text = "Don't have an account? Create account",
                color = Color(0xFF6A6A6A),
                modifier = Modifier.clickable { onCreateAccount() }
            )
        }
    }
}
