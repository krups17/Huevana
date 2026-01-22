package org.example.presentation

import domain.Model
import domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.collections.emptyList

class NotificationsViewModel(val model: Model) {

    private val _notifications = MutableStateFlow<List<String>>(emptyList())
    val notifications: StateFlow<List<String>> = _notifications

    suspend fun getCurrentUser(userId: Int): User {
        return model.fetch_user_from_id(userId)
    }

    suspend fun removeNotification(userId: Int, notification: String) {
        val user = model.fetch_user_from_id(userId)
        val updatedNotifications = user.notifications.filter { it != notification }

        model.update_user_notifications(userId, updatedNotifications)
        _notifications.value = updatedNotifications
    }

    suspend fun clearAllNotifications(userId: Int) {
        model.update_user_notifications(userId, emptyList())
        _notifications.value = emptyList()
    }

    suspend fun loadNotifications(userId: Int) {
        val user = model.fetch_user_from_id(userId)
        _notifications.value = user.notifications
    }

    fun getNotificationCount(user: User): Int {
        return user.notifications.size
    }
}