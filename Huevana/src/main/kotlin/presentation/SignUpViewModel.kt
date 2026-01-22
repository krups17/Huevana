package org.example.presentation

import androidx.compose.runtime.mutableStateOf
import domain.Model
import domain.Persona
import domain.User
import io.github.jan.supabase.exceptions.RestException

class SignUpViewModel(val model: Model) {
    val firstname = mutableStateOf("")
    val lastname = mutableStateOf("")
    val email = mutableStateOf("")
    val username = mutableStateOf("")
    val password = mutableStateOf("")

    val isSigningUp = mutableStateOf(false)
    val isSignedUp = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    suspend fun signUp(): Boolean {
        if (isSigningUp.value) return false

        val hasAllFields = firstname.value.isNotBlank() &&
                lastname.value.isNotBlank() &&
                email.value.isNotBlank() &&
                username.value.isNotBlank() &&
                password.value.isNotBlank()

        if (!hasAllFields) {
            errorMessage.value = "Please fill out all fields"
            return false
        }

        errorMessage.value = null
        isSigningUp.value = true

        return try {
            model.signUp(email.value, password.value)

            val defaultPersonaId = model.add_persona(
                Persona(
                    id = 0,
                    name = firstname.value,
                    recommendedProducts = null,
                    colourAnalysisResult = null
                )
            )
            val userId = model.add_user(
                User(
                    id = 0,
                    username = username.value,
                    password = password.value,
                    email = email.value,
                    firstname = firstname.value,
                    lastname = lastname.value,
                    defaultPersona = defaultPersonaId,
                    personas = mutableListOf(defaultPersonaId)
                )
            )

            val success = userId > 0
            model.currentUserId = userId
            model.currentPersonaId = defaultPersonaId
            isSignedUp.value = success
            success
        } catch (e: RestException) {
            val description = e.description ?: e.message.orEmpty()
            errorMessage.value = if (description.contains("user_already_exists", ignoreCase = true) ||
                description.contains("already registered", ignoreCase = true)
            ) {
                "An account with this email already exists. Please sign in."
            } else {
                description.ifBlank { "Sign up failed" }
            }
            false
        } catch (e: Exception) {
            errorMessage.value = e.message ?: "Sign up failed"
            false
        } finally {
            isSigningUp.value = false
        }
    }
}