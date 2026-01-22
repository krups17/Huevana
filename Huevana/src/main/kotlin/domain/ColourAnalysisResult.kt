package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class ColourAnalysisResult(
    val id: Int?,
    val undertone: String,
    val value: String,
    val contrast: String,
    val chroma: String,
    val extractedColours: Int?, // Extracted Colours id
    val palette: Int // palette id containing season and hex codes
)