package domain

import kotlinx.serialization.Serializable
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours

@Serializable
data class Persona(
    val id: Int,
    val name: String,
    var recommendedProducts: MutableList<Int>? = mutableListOf(), // product ids
    val colourAnalysisResult: Int?, // colour analysis result id
    val profilepic: Long? = null, // profile picture id (int8)
)
