package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.presentation.SignUpViewModel

@Composable
fun SignUpView(
    viewModel: SignUpViewModel,
    onSignUp: () -> Unit,
    onSignInClick: () -> Unit,
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
                value = viewModel.firstname.value,
                onValueChange = { viewModel.firstname.value = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.lastname.value,
                onValueChange = { viewModel.lastname.value = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )

            viewModel.errorMessage.value?.let { msg ->
                Text(msg, color = Color(0xFFD32F2F), modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        if (viewModel.signUp()) onSignUp()
                    }
                },
                enabled = !viewModel.isSigningUp.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE95D7A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Text("Sign Up")
            }

            Text(
                text = "Already have an account? Sign in",
                color = Color(0xFF6A6A6A),
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }
}
