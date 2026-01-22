package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.presentation.ChatbotPageViewModel
import androidx.compose.ui.input.key.*

@Composable
fun ChatbotPageView(padding: PaddingValues, viewmodel: ChatbotPageViewModel) {
    var messages by remember { mutableStateOf(listOf(
        "Huevana AI" to "Welcome to Huevana! I'm here to help you discover your personal color palette. Ask me anything!",
    )) }
    var askAi by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val sendMessage: () -> Unit = {
        if (askAi.isNotBlank() && !isLoading) {
            val userMessage = askAi
            messages = messages + ("You" to userMessage)
            askAi = ""
            isLoading = true

            scope.launch {
                val response = viewmodel.sendMessage(userMessage)
                messages = messages + ("Huevana AI" to response)
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF9F7FB)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                "Huevana AI Assistant",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.7f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { (sender, msg) ->
                    Message(msg, sender)
                }

                // Loading indicator
                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF6B4FA3),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Thinking...", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Input box at bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = askAi,
                    onValueChange = { askAi = it },
                    label = { Text("Ask AI") },
                    modifier = Modifier
                        .weight(1f)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown &&
                                keyEvent.key == Key.Enter) {
                                sendMessage()
                                true
                            } else {
                                false
                            }
                        },
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = sendMessage,
                    enabled = !isLoading && askAi.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Send",
                        tint = if (askAi.isNotBlank() && !isLoading) Color(0xFF6B4FA3) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun Message(msg: String, sender: String) {
    val isAI = sender == "Huevana AI"
    val backgroundColor = if (isAI) Color(0xFFE8E4F3) else Color(0xFFFFF0F5)
    val alignment = if (isAI) Arrangement.Start else Arrangement.End

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Card(
            backgroundColor = backgroundColor,
            modifier = Modifier.fillMaxWidth(0.75f),
            elevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
            ) {
                Text(
                    text = sender,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B4FA3),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg,
                    fontSize = 14.sp,
                    color = Color.Black,
                )
            }
        }
    }
}