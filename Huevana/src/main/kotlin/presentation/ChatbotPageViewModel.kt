package org.example.presentation

import domain.Model
import org.example.domain.ChatBot

class ChatbotPageViewModel(val model: Model) {

    suspend fun sendMessage(userMessage: String, colorSeason: String? = null): String {
        return try {
            ChatBot.ChatbotServiceGemini.sendSimpleMessage(userMessage, colorSeason)
        } catch (e: Exception) {
            "Sorry, I encountered an error. Please try again."
        }
    }
}