package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class ExtractedColours(
    val id: Int?,
    val foreheadHex: String,
    val cheekHex: String,
    val chinHex: String,
    val hairHex: String,
    val eyeHex: String?
)
