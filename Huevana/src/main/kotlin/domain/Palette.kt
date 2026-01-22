package org.example.domain

import domain.Product
import kotlinx.serialization.Serializable

@Serializable
data class Palette (
    val id: Int,
    var name: String,
    var hexCodes: MutableList<String>? = mutableListOf(), // 6 character hex codes
    var products: MutableList<Int>? = mutableListOf(), // product ids
    var names: MutableList<String>? = mutableListOf() // name of each hex code colour
)
{
}