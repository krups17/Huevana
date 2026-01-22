package org.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.sarxos.webcam.Webcam
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.presentation.AccountPageViewModel
import java.awt.image.BufferedImage


@Composable
fun AccountPageView(
    padding: PaddingValues,
    viewModel: AccountPageViewModel,
    onLogout: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF9F7FB))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {


            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                OutlinedTextField(
                    value = viewModel.fn.value,
                    onValueChange = { viewModel.fn.value = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                OutlinedTextField(
                    value = viewModel.ln.value,
                    onValueChange = { viewModel.ln.value = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.username.value = it },
                label = { Text("User Name") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // change this to a better password changer
            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save message
            if (viewModel.saveMessage.value.isNotEmpty()) {
                Text(
                    text = viewModel.saveMessage.value,
                    fontSize = 14.sp,
                    color = if (viewModel.saveMessage.value.contains("Error")) Color.Red else Color(0xFF4CAF50),
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    scope.launch {
                        viewModel.saveChanges()
                    }
                },
                enabled = !viewModel.isSaving.value,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE95D7A),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(if (viewModel.isSaving.value) "Saving..." else "Save Changes")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onLogout,
                border = ButtonDefaults.outlinedBorder,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Log Out", color = Color(0xFFE95D7A))
            }
        }
    }
}
