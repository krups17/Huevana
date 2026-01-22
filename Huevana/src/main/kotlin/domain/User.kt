package domain

import kotlinx.serialization.Serializable

/** User.kt
 * Data class used for storing account information.
 */

// one account per email but can have multiple personas (each persona is associated with a palette)
@Serializable
data class User(
    val id: Int,
    var username: String,
    var password: String,
    var email: String,
    var firstname: String,
    var lastname: String,

    var friends: MutableList<Int> = mutableListOf(), // user ids

    var pastProducts: MutableList<Int> = mutableListOf(), // product ids
    var currentProducts: MutableList<Int> = mutableListOf(), // product ids

    var wishList: MutableList<Int> = mutableListOf(), // product ids

    var defaultPersona: Int? = null, // persona id
    var personas: MutableList<Int> = mutableListOf(), // persona ids
    var sharedPersonas: MutableList<Int>? = null,  // shared persona ids

    var notifications: MutableList<String> = mutableListOf(),
)
