package org.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class StoreRating (
    val id: Int,
    val store: String,
    var rating: Int, // between 1 and 5
    var product: Int, // product id
){
}