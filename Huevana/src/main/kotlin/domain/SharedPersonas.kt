package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class SharedPersonas (
    val id: Int,
    val persona: Int,  // persona id
    val owner: Int  // user id
)
