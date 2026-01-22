package org.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.Palette
import org.example.presentation.ResultsViewModel

@Composable
fun ResultsPageView(
    padding: PaddingValues,
    result: ColourAnalysisResult,
    onStartOver: () -> Unit,
    viewModel: ResultsViewModel,
) {
    var paletteName by remember { mutableStateOf("Loading...") }
    var palette by remember { mutableStateOf<Palette?>(null) }
    var extracted_colours by remember { mutableStateOf<ExtractedColours?>(null) }

    LaunchedEffect(result.id) {
        println("=== FETCHING DATA FOR RESULTS ===")
        println("ColourAnalysisResult ID: ${result.id}")
        println("Looking for Palette ID: ${result.palette}")
        println("Looking for ExtractedColours ID: ${result.extractedColours}")

        palette = viewModel.fetch_palette_from_id(result.palette)
        paletteName = palette?.name ?: "Unknown"

        extracted_colours = viewModel.fetch_extract_colours_from_id(result.extractedColours)

        println("Fetched ExtractedColours: $extracted_colours")
        println("Forehead: ${extracted_colours?.foreheadHex}")
        println("Cheek: ${extracted_colours?.cheekHex}")
        println("Chin: ${extracted_colours?.chinHex}")
        println("Hair: ${extracted_colours?.hairHex}")
        println("Eye: ${extracted_colours?.eyeHex}")
        println("=== END FETCHING ===")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFE1E5F2))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            "Your Color Analysis",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF000000)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = Color.White,
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    "Your Color Season",
                    fontSize = 18.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    paletteName,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE95D7A)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = Color.White,
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    "Analysis Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnalysisRow("Undertone", result.undertone.replaceFirstChar { it.uppercase() })
                Spacer(modifier = Modifier.height(12.dp))
                AnalysisRow("Value", result.value.replaceFirstChar { it.uppercase() })
                Spacer(modifier = Modifier.height(12.dp))
                AnalysisRow("Contrast", result.contrast.replaceFirstChar { it.uppercase() })
                Spacer(modifier = Modifier.height(12.dp))
                AnalysisRow("Chroma", result.chroma.replaceFirstChar { it.uppercase() })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = Color.White,
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    "Extracted Colors",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ColorSwatch("Forehead", extracted_colours?.foreheadHex ?: "000000")
                    ColorSwatch("Cheek", extracted_colours?.cheekHex ?: "000000")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ColorSwatch("Chin", extracted_colours?.chinHex ?: "000000")
                    ColorSwatch("Hair", extracted_colours?.hairHex ?: "000000")
                }

                extracted_colours?.eyeHex?.let { eyeHex ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ColorSwatch("Eyes", eyeHex)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = Color.White,
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    "Your Perfect Palette",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "These colors will make you look your best:",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val hexCodes = palette?.hexCodes ?: emptyList()
                val names = palette?.names ?: emptyList()

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(hexCodes.size) { index ->
                        val hex = hexCodes[index].removePrefix("#")
                        val name = names.getOrNull(index) ?: ""
                        PaletteColorBox(hexString = hex, name = name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartOver,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFE95D7A),
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text("Start Over", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AnalysisRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color(0xFF666666)
        )

        Card(
            backgroundColor = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ColorSwatch(label: String, hexColor: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = hexToComposeColor(hexColor),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFFE0E0E0),
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )

        Text(
            text = "#$hexColor",
            fontSize = 10.sp,
            color = Color(0xFF999999)
        )
    }
}

@Composable
fun PaletteColorBox(hexString: String, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = hexToComposeColor(hexString),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (name.isNotBlank()) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }

        Text(
            text = "#${hexString.uppercase()}",
            fontSize = 11.sp,
            color = Color(0xFF666666)
        )
    }
}

fun hexToComposeColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return try {
        val colorInt = cleanHex.toLong(16).toInt()
        Color(
            red = ((colorInt shr 16) and 0xFF) / 255f,
            green = ((colorInt shr 8) and 0xFF) / 255f,
            blue = (colorInt and 0xFF) / 255f
        )
    } catch (e: Exception) {
        Color.Black
    }
}
