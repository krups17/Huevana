package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class HexCode (
    var id: Long,
    var hexCode: String // 6 character hex
) {
}