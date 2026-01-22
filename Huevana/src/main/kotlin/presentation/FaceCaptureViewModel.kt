package org.example.presentation

import androidx.compose.ui.graphics.ImageBitmap
import domain.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.domain.ColourAnalysis
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.Palette
import java.awt.image.BufferedImage

class FaceCaptureViewModel (val model: Model) {
    val colourAnalysis = ColourAnalysis(this)
    val imageColourExtractor = colourAnalysis.ImageColourExtractor()
    val colourAnalysisService = colourAnalysis.ColourAnalysisService()

    private val _uiState = MutableStateFlow<FaceCaptureUiState>(FaceCaptureUiState.Capturing)
    val uiState: StateFlow<FaceCaptureUiState> = _uiState.asStateFlow()

    private val _webcamImage = MutableStateFlow<ImageBitmap?>(null)
    val webcamImage: StateFlow<ImageBitmap?> = _webcamImage.asStateFlow()

    private val _stillCounter = MutableStateFlow(0)
    val stillCounter: StateFlow<Int> = _stillCounter.asStateFlow()

    fun updateWebcamImage(image: ImageBitmap) {
        _webcamImage.value = image
    }

    fun updateStillCounter(count: Int) {
        _stillCounter.value = count
    }

    fun captureImage(bufferedImage: BufferedImage, imageBitmap: ImageBitmap) {
        _uiState.value = FaceCaptureUiState.Captured(imageBitmap)
    }

    fun retakePhoto() {
        _uiState.value = FaceCaptureUiState.Capturing
        _stillCounter.value = 0
    }

    suspend fun analyzeImage(bufferedImage: BufferedImage, personaId: Int) {
        _uiState.value = FaceCaptureUiState.Analyzing

        try {
            val extractedColors = imageColourExtractor.extractColoursFromFace(bufferedImage)

            val result = colourAnalysisService.analyzeColours(extractedColors, personaId = personaId)

            _uiState.value = FaceCaptureUiState.ShowingResults(result)
        } catch (e: Exception) {
            _uiState.value = FaceCaptureUiState.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun resetAnalysis() {
        _uiState.value = FaceCaptureUiState.Capturing
        _stillCounter.value = 0
    }

    suspend fun add_CAR(CAR: ColourAnalysisResult): Int? {
        return model.add_CAR(CAR)
    }

    suspend fun add_extract_colours(extracted_colours: ExtractedColours): Int? {
        return model.add_extract_colours(extracted_colours)
    }


    suspend fun fetch_palette_from_name(name: String): Palette {
        return model.fetch_palette_from_name(name)
    }

    suspend fun analyzeWithExtractedColors(extractedColours: ExtractedColours, personaId: Int) {
        println("=== ANALYZE WITH EXTRACTED COLORS ===")
        println("Received ExtractedColours with ID: ${extractedColours.id}")

        _uiState.value = FaceCaptureUiState.Analyzing

        try {
            val colourAnalysisService = ColourAnalysis(this).ColourAnalysisService()
            val result = colourAnalysisService.analyzeColours(extractedColours, personaId = personaId)

            println("Analysis complete. Result: $result")
            _uiState.value = FaceCaptureUiState.ShowingResults(result)
        } catch (e: Exception) {
            println("ERROR in analysis: ${e.message}")
            e.printStackTrace()
            _uiState.value = FaceCaptureUiState.Error("Analysis failed: ${e.message}")
        }
    }
    suspend fun update_persona_colour_analysis_result(persona_id: Int, new_CAR_id: Int?) {
        return model.update_persona_colour_analysis_result(persona_id, new_CAR_id, model.currentUserId)
    }



}

open class FaceCaptureUiState {
    object Capturing : FaceCaptureUiState()
    data class Captured(val image: ImageBitmap) : FaceCaptureUiState()
    object Analyzing : FaceCaptureUiState()
    data class ShowingResults(val result: ColourAnalysisResult) : FaceCaptureUiState()
    data class Error(val message: String) : FaceCaptureUiState()
}