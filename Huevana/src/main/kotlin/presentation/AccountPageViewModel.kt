package org.example.presentation

import androidx.compose.runtime.mutableStateOf
import domain.Model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountPageViewModel(val model: Model) {
    var fn = mutableStateOf("")
    var ln = mutableStateOf("")
    var email = mutableStateOf("")
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var isSaving = mutableStateOf(false)
    var saveMessage = mutableStateOf("")

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            val user = model.fetch_user_from_id(model.currentUserId)

            fn.value = user.firstname
            ln.value = user.lastname
            email.value = user.email
            username.value = user.username
            password.value = user.password
        }
    }

    suspend fun saveChanges() {
        if (model.currentUserId == -1) {
            saveMessage.value = "Error: User not logged in"
            return
        }

        isSaving.value = true
        saveMessage.value = ""

        try {
            // Update in-memory cache
            model.updateFirstName(model.currentUserId, fn.value)
            model.updateLastName(model.currentUserId, ln.value)
            model.updateEmail(model.currentUserId, email.value)
            model.updateUsername(model.currentUserId, username.value)

            // Update password if it's different
            val currentUser = model.fetch_user_from_id(model.currentUserId)
            if (currentUser.password != password.value) {
                // Update password - using current password as old password since we're allowing direct updates
                // In a production app, you'd want to verify the old password separately
                model.updatePassword(model.currentUserId, currentUser.password, password.value)
                // Update password in database
                model.update_user_column_string(model.currentUserId, "password", password.value)
            }

            // Persist all changes to database
            model.update_user_column_string(model.currentUserId, "firstname", fn.value)
            model.update_user_column_string(model.currentUserId, "lastname", ln.value)
            model.update_user_column_string(model.currentUserId, "email", email.value)
            model.update_user_column_string(model.currentUserId, "username", username.value)

            saveMessage.value = "Changes saved successfully!"
        } catch (e: Exception) {
            saveMessage.value = "Error saving changes: ${e.message}"
            println("Error saving account changes: ${e.message}")
            e.printStackTrace()
        } finally {
            isSaving.value = false
        }
    }
}