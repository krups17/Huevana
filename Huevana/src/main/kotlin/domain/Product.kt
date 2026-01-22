package domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.domain.Palette

@Serializable
data class Product(
    val id: Int,
    var name: String,
    var brand: String,
    @SerialName("imageurl")
    var imageUrl: String,
    var palette: Int,
    var rating: Float,
    var shade: String
)
