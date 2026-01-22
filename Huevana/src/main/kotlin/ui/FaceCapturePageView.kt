package org.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.sarxos.webcam.Webcam
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.presentation.FaceCaptureUiState
import org.example.presentation.FaceCaptureViewModel
import java.awt.image.BufferedImage
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import org.example.presentation.ResultsViewModel
import androidx.compose.foundation.gestures.detectTapGestures
import org.example.domain.ExtractedColours
import kotlin.math.roundToInt
@Composable
fun FaceCapturePageView(padding: PaddingValues, viewModel: FaceCaptureViewModel, personaId: Int) {
    val uiState by viewModel.uiState.collectAsState()
    val webcamImage by viewModel.webcamImage.collectAsState()
    val stillCounter by viewModel.stillCounter.collectAsState()

    var isActive by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val webcam = remember { Webcam.getDefault() }
    val previousImage = remember { mutableStateOf<BufferedImage?>(null) }
    val currentBufferedImage = remember { mutableStateOf<BufferedImage?>(null) }
    val capturedBufferedImage = remember { mutableStateOf<BufferedImage?>(null) }

    DisposableEffect(Unit) {
        webcam?.open()

        val job = scope.launch {
            while (isActive && webcam?.isOpen == true) {
                val currentImage = webcam.image
                currentImage?.let { image ->
                    currentBufferedImage.value = image
                    viewModel.updateWebcamImage(image.toComposeImageBitmap())

                    if (uiState is FaceCaptureUiState.Capturing && previousImage.value != null) {
                        val isStill = checkIfStill(previousImage.value!!, image)

                        if (isStill) {
                            val newCounter = stillCounter + 1
                            viewModel.updateStillCounter(newCounter)
                            if (newCounter >= 60) {
                                capturedBufferedImage.value = image
                                viewModel.captureImage(image, image.toComposeImageBitmap())
                            }
                        } else {
                            viewModel.updateStillCounter(0)
                        }
                    }

                    previousImage.value = image
                }
                delay(33)
            }
        }

        onDispose {
            isActive = false
            job.cancel()
            webcam?.close()
        }
    }

    when (val state = uiState) {
        is FaceCaptureUiState.Capturing -> {
            CapturingView(
                padding = padding,
                webcamImage = webcamImage,
                stillCounter = stillCounter,
                onCaptureNow = {
                    currentBufferedImage.value?.let { buffImage ->
                        webcamImage?.let { imgBitmap ->
                            capturedBufferedImage.value = buffImage
                            viewModel.captureImage(buffImage, imgBitmap)
                        }
                    }
                }
            )
        }
        is FaceCaptureUiState.Captured -> {
            ColorPickerView(
                padding = padding,
                image = state.image,
                bufferedImage = capturedBufferedImage.value,
                onRetake = {
                    capturedBufferedImage.value = null
                    viewModel.retakePhoto()
                },
                onAnalyze = { selectedColors ->

                    capturedBufferedImage.value?.let { buffImage ->
                        scope.launch {
                            val skinColor = selectedColors.find { it.name == "Skin Tone" }?.hex?.removePrefix("#") ?: "d4a894"
                            val hairColor = selectedColors.find { it.name == "Hair Color" }?.hex?.removePrefix("#") ?: "3b2a1f"
                            val eyeColor = selectedColors.find { it.name == "Eye Color" }?.hex?.removePrefix("#")


                            val extractedColours = ExtractedColours(
                                id = null,
                                foreheadHex = skinColor,
                                cheekHex = skinColor,
                                chinHex = skinColor,
                                hairHex = hairColor,
                                eyeHex = eyeColor
                            )

                            val ecId = viewModel.add_extract_colours(extractedColours)

                            val updatedEC = extractedColours.copy(id = ecId)

                            viewModel.analyzeWithExtractedColors(updatedEC, personaId = personaId)
                        }
                    } ?: run {
                    }
                }
            )
        }
        is FaceCaptureUiState.Analyzing -> {
            AnalyzingView(padding = padding)
        }
        is FaceCaptureUiState.ShowingResults -> {
            ResultsPageView(
                padding = padding,
                result = state.result,
                onStartOver = {
                    capturedBufferedImage.value = null
                    viewModel.resetAnalysis()
                },
                viewModel = ResultsViewModel(
                    model = viewModel.model
                )
            )
        }
        is FaceCaptureUiState.Error -> {
            ErrorView(
                padding = padding,
                message = state.message,
                onRetry = {
                    capturedBufferedImage.value = null
                    viewModel.retakePhoto()
                }
            )
        }
    }
}

data class SelectedColor(val name: String, val hex: String, val color: Color)

@Composable
private fun ColorPickerView(
    padding: PaddingValues,
    image: ImageBitmap,
    bufferedImage: BufferedImage?,
    onRetake: () -> Unit,
    onAnalyze: (List<SelectedColor>) -> Unit
) {
    var selectedColors by remember { mutableStateOf<List<SelectedColor>>(emptyList()) }
    var currentColorSlot by remember { mutableStateOf(0) }

    val colorSlots = listOf(
        "Skin Tone",
        "Hair Color",
        "Eye Color",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFE1E5F2))
            .padding(16.dp)
    ) {
        Text(
            "Select Your Colors",
            fontSize = 28.sp,
            color = Color(0xFF333333),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            "Click on your photo to pick ${colorSlots[currentColorSlot]}",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .width(480.dp)
                .height(360.dp),
            backgroundColor = Color.Black
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            bufferedImage?.let { img ->
                                val imgWidth = img.width.toFloat()
                                val imgHeight = img.height.toFloat()
                                val viewWidth = size.width.toFloat()
                                val viewHeight = size.height.toFloat()

                                val mirroredX = viewWidth - offset.x

                                val x = (mirroredX / viewWidth * imgWidth).roundToInt().coerceIn(0, img.width - 1)
                                val y = (offset.y / viewHeight * imgHeight).roundToInt().coerceIn(0, img.height - 1)

                                val rgb = img.getRGB(x, y)
                                val r = (rgb shr 16) and 0xFF
                                val g = (rgb shr 8) and 0xFF
                                val b = rgb and 0xFF

                                val hexColor = String.format("#%02X%02X%02X", r, g, b)
                                val newColor = SelectedColor(
                                    name = colorSlots[currentColorSlot],
                                    hex = hexColor,
                                    color = Color(r, g, b)
                                )

                                val updatedColors = selectedColors.toMutableList()
                                val existingIndex = updatedColors.indexOfFirst { it.name == colorSlots[currentColorSlot] }

                                if (existingIndex >= 0) {
                                    updatedColors[existingIndex] = newColor
                                } else {
                                    updatedColors.add(newColor)
                                }

                                selectedColors = updatedColors

                                if (currentColorSlot < colorSlots.size - 1) {
                                    currentColorSlot++
                                }
                            }
                        }
                    }
            ) {
                Image(
                    bitmap = image,
                    contentDescription = "Captured face",
                    modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = -1f)
                )


            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            colorSlots.forEachIndexed { index, slotName ->
                val selectedColor = selectedColors.find { it.name == slotName }
                ColorSlotCard(
                    slotName = slotName,
                    selectedColor = selectedColor,
                    isActive = currentColorSlot == index,
                    onClick = { currentColorSlot = index },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRetake,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF888888),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
            ) {
                Text("Retake", fontSize = 16.sp)
            }

            Button(
                onClick = { onAnalyze(selectedColors) },
                enabled = selectedColors.size >= 2,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE95D7A),
                    contentColor = Color.White,
                    disabledBackgroundColor = Color(0xFFCCCCCC)
                ),
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
            ) {
                Text("Analyze", fontSize = 16.sp)
            }
        }

        if (selectedColors.size < 2) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Please select at least 2 colors",
                fontSize = 14.sp,
                color = Color(0xFFE95D7A)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ColorSlotCard(
    slotName: String,
    selectedColor: SelectedColor?,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (isActive) Color(0xFFE95D7A).copy(alpha = 0.1f) else Color.White,
        elevation = if (isActive) 8.dp else 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(6.dp)
        ) {
            Text(
                slotName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color(0xFFE95D7A) else Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (selectedColor != null) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(selectedColor.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    selectedColor.hex,
                    fontSize = 9.sp,
                    color = Color(0xFF666666)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(Color(0xFFEEEEEE), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", fontSize = 18.sp, color = Color(0xFFCCCCCC))
                }
            }
        }
    }
}

@Composable
private fun ColorSlotCard(
    slotName: String,
    selectedColor: SelectedColor?,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (isActive) Color(0xFFE95D7A).copy(alpha = 0.1f) else Color.White,
        elevation = if (isActive) 8.dp else 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                slotName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color(0xFFE95D7A) else Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedColor != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(selectedColor.color, shape = CircleShape)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    selectedColor.hex,
                    fontSize = 10.sp,
                    color = Color(0xFF666666)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEEEEEE), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("?", fontSize = 20.sp, color = Color(0xFFCCCCCC))
                }
            }
        }
    }
}

@Composable
private fun CapturingView(
    padding: PaddingValues,
    webcamImage: ImageBitmap?,
    stillCounter: Int,
    onCaptureNow: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFE1E5F2))
            .padding(24.dp)
    ) {
        Text(
            "Face Capture",
            fontSize = 32.sp,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val statusText = when {
            stillCounter > 30 -> "Capturing in ${3 - (stillCounter / 20)}..."
            stillCounter > 0 -> "Hold still..."
            else -> "Position your face in the oval and stay still"
        }

        Text(
            statusText,
            fontSize = 16.sp,
            color = if (stillCounter > 0) Color(0xFF4CAF50) else Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .width(640.dp)
                .height(480.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                backgroundColor = Color.Black
            ) {
                when {
                    webcamImage != null -> {
                        Image(
                            bitmap = webcamImage,
                            contentDescription = "Webcam feed",
                            modifier = Modifier.fillMaxSize().graphicsLayer(scaleX = -1f)
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Loading camera...", color = Color.White)
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val ovalWidth = canvasWidth * 0.5f
                val ovalHeight = canvasHeight * 0.8f
                val ovalLeft = (canvasWidth - ovalWidth) / 2f
                val ovalTop = (canvasHeight - ovalHeight) / 2f

                val fullPath = Path()
                fullPath.moveTo(0f, 0f)
                fullPath.lineTo(canvasWidth, 0f)
                fullPath.lineTo(canvasWidth, canvasHeight)
                fullPath.lineTo(0f, canvasHeight)
                fullPath.close()

                val ovalPath = Path()
                ovalPath.addOval(
                    Rect(
                        left = ovalLeft,
                        top = ovalTop,
                        right = ovalLeft + ovalWidth,
                        bottom = ovalTop + ovalHeight
                    )
                )

                val overlayPath = Path.combine(
                    operation = PathOperation.Difference,
                    path1 = fullPath,
                    path2 = ovalPath
                )

                drawPath(
                    path = overlayPath,
                    color = Color.Black.copy(alpha = 0.6f)
                )

                val borderColor = when {
                    stillCounter > 30 -> Color(0xFF4CAF50)
                    stillCounter > 0 -> Color(0xFFFFEB3B)
                    else -> Color.White
                }

                drawOval(
                    color = borderColor,
                    topLeft = Offset(ovalLeft, ovalTop),
                    size = Size(ovalWidth, ovalHeight),
                    style = Stroke(width = 4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCaptureNow,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFE95D7A),
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(250.dp)
                .height(50.dp)
        ) {
            Text("Capture Now (Skip Auto)", fontSize = 16.sp)
        }
    }
}

@Composable
private fun AnalyzingView(padding: PaddingValues) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFE1E5F2))
            .padding(24.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = Color(0xFFE95D7A),
            strokeWidth = 6.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Analyzing Your Colors...",
            fontSize = 24.sp,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "This may take a few moments",
            fontSize = 16.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun ErrorView(
    padding: PaddingValues,
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFE1E5F2))
            .padding(24.dp)
    ) {
        Text(
            "Error",
            fontSize = 32.sp,
            color = Color(0xFFFF5252)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            message,
            fontSize = 16.sp,
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFE95D7A),
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Try Again", fontSize = 16.sp)
        }
    }
}

fun checkIfStill(previous: BufferedImage, current: BufferedImage): Boolean {
    if (previous.width != current.width || previous.height != current.height) {
        return false
    }

    var differences = 0
    val threshold = 30
    val sampleRate = 10
    val maxDifferences = (previous.width * previous.height) / (sampleRate * sampleRate * 20)

    for (y in 0 until previous.height step sampleRate) {
        for (x in 0 until previous.width step sampleRate) {
            val rgb1 = previous.getRGB(x, y)
            val rgb2 = current.getRGB(x, y)

            val r1 = (rgb1 shr 16) and 0xFF
            val g1 = (rgb1 shr 8) and 0xFF
            val b1 = rgb1 and 0xFF

            val r2 = (rgb2 shr 16) and 0xFF
            val g2 = (rgb2 shr 8) and 0xFF
            val b2 = rgb2 and 0xFF

            val diff = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2)

            if (diff > threshold) {
                differences++
                if (differences > maxDifferences) {
                    return false
                }
            }
        }
    }

    return true
}