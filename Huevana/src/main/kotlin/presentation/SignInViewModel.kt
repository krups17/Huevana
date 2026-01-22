package org.example.presentation

import androidx.compose.runtime.mutableStateOf
import domain.Model

class SignInViewModel(val model: Model) {
    val username = mutableStateOf("")
    val password = mutableStateOf("")

    val isSigningIn = mutableStateOf(false)
    val isSignedIn = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    suspend fun signIn(): Boolean {
        if (isSigningIn.value) return false

        val hasAllFields = username.value.isNotBlank() && password.value.isNotBlank()
        if (!hasAllFields) {
            errorMessage.value = "Please enter username and password"
            return false
        }

        errorMessage.value = null
        isSigningIn.value = true

        return try {
            val userId = model.signIn(username.value, password.value)
            val user = model.fetch_user_from_id(userId)
            model.currentUserId = userId
            model.currentPersonaId = user.defaultPersona ?: -1
            isSignedIn.value = true
            true
        } catch (e: Exception) {
            errorMessage.value = e.message ?: "Sign in failed"
            false
        } finally {
            isSigningIn.value = false
        }
    }
}