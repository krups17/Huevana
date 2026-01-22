package org.example.presentation

import domain.Model
import org.example.domain.ExtractedColours
import org.example.domain.Palette

class ResultsViewModel (val model: Model){
    suspend fun fetch_palette_from_name(name: String): Palette {
        return model.fetch_palette_from_name(name)
    }
    suspend fun fetch_palette_from_id(id: Int?): Palette {
        return model.fetch_palette_from_id(id)
    }
    suspend fun fetch_extract_colours_from_id(id: Int?): ExtractedColours {
        return model.fetch_extract_colours_from_id(id)
    }
}