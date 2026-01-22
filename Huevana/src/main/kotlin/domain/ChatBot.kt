package org.example.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.net.HttpURLConnection
import java.net.URL

class ChatBot {

    @Serializable
    data class GeminiRequest(
        val contents: List<Content>
    )

    @Serializable
    data class Content(
        val parts: List<Part>
    )

    @Serializable
    data class Part(
        val text: String
    )

    @Serializable
    data class GeminiResponse(
        val candidates: List<Candidate>
    )

    @Serializable
    data class Candidate(
        val content: Content
    )

    object ChatbotServiceGemini {
        private const val API_URL = "https://generativelanguage.googleapis.com"
        private const val API_KEY = "AIzaSyCsC6DL4Y9XXUiAJN_4fOuaDTxzSJDzoL4"

        private fun loadApiKeyFromProperties(): String? {
            return try {
                val properties = java.util.Properties()
                val propertiesFile = java.io.File("local.properties")
                if (propertiesFile.exists()) {
                    properties.load(propertiesFile.inputStream())
                    properties.getProperty("GEMINI_API_KEY")
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        private const val SYSTEM_PROMPT = """You are a helpful beauty and color analysis assistant. 
            Provide expert advice on seasonal color analysis, makeup, outfit coordination, and personal style. 
            Be friendly and concise (2-3 paragraphs)."""

        private val json = Json { ignoreUnknownKeys = true }

        suspend fun sendSimpleMessage(userMessage: String, colorSeason: String? = null): String =
            withContext(Dispatchers.IO) {
                try {
                    // DEBUG: Print the API key (first/last 4 chars only for security)
                    println("API Key loaded: ${API_KEY.take(4)}...${API_KEY.takeLast(4)}")
                    println("API Key length: ${API_KEY.length}")

                    if (API_KEY == "test-key-not-set") {
                        return@withContext "API key not configured. Please add GEMINI_API_KEY to local.properties"
                    }

                    val systemPrompt = if (colorSeason != null) {
                        "$SYSTEM_PROMPT\n\nUser's color season: $colorSeason"
                    } else SYSTEM_PROMPT

                    val fullPrompt = "$systemPrompt\n\nUser question: $userMessage"

                    val requestBody = GeminiRequest(
                        contents = listOf(
                            Content(
                                parts = listOf(Part(text = fullPrompt))
                            )
                        )
                    )

                    val fullUrl = "$API_URL/v1beta/models/gemini-2.5-flash:generateContent?key=$API_KEY"

                    val connection = (URL(fullUrl).openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        doOutput = true
                    }

                    connection.outputStream.use {
                        it.write(json.encodeToString(requestBody).toByteArray())
                    }

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        val geminiResponse = json.decodeFromString<GeminiResponse>(response)
                        geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                            ?: "I couldn't generate a response. Please try again."
                    } else {
                        val errorBody =
                            connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No error details"
                        println("Gemini API Error - Code: $responseCode, Body: $errorBody")

                        when (responseCode) {
                            401, 403 -> "API key is invalid or missing. Please check your Gemini API key."
                            429 -> "Rate limit exceeded. Please try again later."
                            else -> "Connection error (Code: $responseCode). Please check your API key and try again."
                        }
                    }
                } catch (e: Exception) {
                    println("Exception in ChatBot: ${e.message}")
                    e.printStackTrace()
                    "Error: ${e.message}"
                }
            }
    }
}
