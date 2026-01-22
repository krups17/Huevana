package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class FESharedPersonas (
    val SPid: Int,  // shared persona id
    val persona: String,  // persona name
    val personaId: Int,  // persona ID
    val owner: String,  // user first name
    val profilepic: Long? = null  // profile picture id (int8)
)
