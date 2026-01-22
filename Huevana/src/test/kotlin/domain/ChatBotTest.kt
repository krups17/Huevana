package org.example.domain

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChatBotTest {

    private val json = Json { ignoreUnknownKeys = true }

    // Test: Parse valid Gemini response
    @Test
    fun testParseValidResponse() {
        val jsonResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "Hello! I can help with color analysis."
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val response = json.decodeFromString<ChatBot.GeminiResponse>(jsonResponse)

        assertEquals("Hello! I can help with color analysis.",
            response.candidates[0].content.parts[0].text)
    }

    // Test: Parse response with empty candidates
    @Test
    fun testParseEmptyResponse() {
        val jsonResponse = """
            {
                "candidates": []
            }
        """.trimIndent()

        val response = json.decodeFromString<ChatBot.GeminiResponse>(jsonResponse)

        assertTrue(response.candidates.isEmpty())
    }

    // Test: Parse response ignoring unknown fields
    @Test
    fun testIgnoreUnknownFields() {
        val jsonResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "Response text",
                                    "unknownField": "ignored"
                                }
                            ]
                        },
                        "extraField": "also ignored"
                    }
                ],
                "metadata": "ignored too"
            }
        """.trimIndent()

        val response = json.decodeFromString<ChatBot.GeminiResponse>(jsonResponse)

        assertEquals("Response text", response.candidates[0].content.parts[0].text)
    }

    // Test: Parse response with multiple parts
    @Test
    fun testMultipleParts() {
        val jsonResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "Part 1"
                                },
                                {
                                    "text": "Part 2"
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val response = json.decodeFromString<ChatBot.GeminiResponse>(jsonResponse)

        assertEquals(2, response.candidates[0].content.parts.size)
        assertEquals("Part 1", response.candidates[0].content.parts[0].text)
        assertEquals("Part 2", response.candidates[0].content.parts[1].text)
    }

    // Test: Parse response with multiple candidates
    @Test
    fun testMultipleCandidates() {
        val jsonResponse = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "First response"
                                }
                            ]
                        }
                    },
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "Second response"
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val response = json.decodeFromString<ChatBot.GeminiResponse>(jsonResponse)

        assertEquals(2, response.candidates.size)
        assertEquals("First response", response.candidates[0].content.parts[0].text)
        assertEquals("Second response", response.candidates[1].content.parts[0].text)
    }

    // Test: Serialize request with multiple parts
    @Test
    fun testSerializeMultipleParts() {
        val request = ChatBot.GeminiRequest(
            contents = listOf(
                ChatBot.Content(
                    parts = listOf(
                        ChatBot.Part(text = "Question 1"),
                        ChatBot.Part(text = "Question 2")
                    )
                )
            )
        )

        val jsonString = json.encodeToString(ChatBot.GeminiRequest.serializer(), request)

        assertTrue(jsonString.contains("Question 1"))
        assertTrue(jsonString.contains("Question 2"))
    }


}