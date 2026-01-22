package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class TutorialVideo (
    val id: Int,
    var created_at: String,
    var name: String,
    var url: String
){
}