package org.example

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import desktop.presentation.ViewModel
import domain.Model
import org.example.data.DB
import org.example.ui.View

fun main() {

//    println("Enter your name: ")
//    val name = readln()
//    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//    // to see how IntelliJ IDEA suggests fixing it.
//    println("Hi, " + name + "!" + " Welcome to Huevana!")
    application {
        Window(
            title = "Huevana",
            state = rememberWindowState(
                position = WindowPosition(Alignment.Center),
                size = DpSize(1500.dp, 900.dp)
            ),
            onCloseRequest = ::exitApplication,
        ) {
            val storage = remember { DB("database") }
            val model = remember { Model(storage) }
            val viewModel = remember { ViewModel(model) }
            View(viewModel)
        }
    }
}