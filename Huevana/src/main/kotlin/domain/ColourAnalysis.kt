package org.example.domain

import org.example.presentation.FaceCaptureViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import java.awt.image.BufferedImage


class ColourAnalysis(val viewModel: FaceCaptureViewModel) {

    data class ColourRGB(val r: Int, val g: Int, val b: Int)
    data class ColourHSL(val h: Double, val s: Double, val l: Double)
    data class Region(
        val xStart: Int,
        val xEnd: Int,
        val yStart: Int,
        val yEnd: Int
    )
    data class PaletteColor(val hex: Int, val name: String)

    inner class ColourAnalysisService {

        val palettes = mapOf(
            "Cool Winter" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0x9ea0a2, "Cool Gray"),
                PaletteColor(0x98c5a8, "Sage Green"),
                PaletteColor(0x345a5d, "Dark Teal"),
                PaletteColor(0xc0c2e1, "Soft Lavender"),
                PaletteColor(0x96b3e0, "Periwinkle Blue"),
                PaletteColor(0x3597c1, "Teal Blue"),
                PaletteColor(0x1d1951, "Deep Navy"),
                PaletteColor(0xeeb0c3, "Dusty Pink"),
                PaletteColor(0x763751, "Burgundy"),
                PaletteColor(0x5d447a, "Dark Purple"),
                PaletteColor(0x7062a9, "Medium Purple")
            ),
            "Bright Winter" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0x000000, "Black"),
                PaletteColor(0x1d1951, "Deep Navy"),
                PaletteColor(0x3597c1, "Turquoise Blue"),
                PaletteColor(0x96b3e0, "Soft Blue"),
                PaletteColor(0x98c5a8, "Mint Green"),
                PaletteColor(0x66af59, "Bright Green"),
                PaletteColor(0xf277ac, "Hot Pink"),
                PaletteColor(0xeeb0c3, "Light Pink"),
                PaletteColor(0x7062a9, "Medium Purple"),
                PaletteColor(0x5d447a, "Dark Purple"),
                PaletteColor(0xd9242c, "True Red")
            ),
            "Dark Winter" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0x000000, "Black"),
                PaletteColor(0x6d6f73, "Dark Gray"),
                PaletteColor(0x9ea0a2, "Medium Gray"),
                PaletteColor(0x1d1951, "Deep Navy"),
                PaletteColor(0x345a5d, "Dark Teal"),
                PaletteColor(0x365f8a, "Deep Blue"),
                PaletteColor(0x3597c1, "Bright Turquoise"),
                PaletteColor(0xf277ac, "Hot Pink"),
                PaletteColor(0x5d447a, "Deep Purple"),
                PaletteColor(0x750c1b, "Burgundy"),
                PaletteColor(0xd9242c, "True Red")
            ),
            "Light Spring" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0xe1c6b2, "Light Beige"),
                PaletteColor(0xad8e79, "Tan"),
                PaletteColor(0x9ea0a2, "Soft Gray"),
                PaletteColor(0x98c5a8, "Sage Green"),
                PaletteColor(0x66af59, "Bright Green"),
                PaletteColor(0x3597c1, "Teal Blue"),
                PaletteColor(0xc0c2e1, "Soft Lavender"),
                PaletteColor(0xf277ac, "Hot Pink"),
                PaletteColor(0xeeb0c3, "Light Pink"),
                PaletteColor(0xf3b357, "Peach"),
                PaletteColor(0xfbe29f, "Light Yellow")
            ),
            "Bright Spring" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0x9ea0a2, "Cool Gray"),
                PaletteColor(0x1d1951, "Deep Navy"),
                PaletteColor(0x3597c1, "Turquoise Blue"),
                PaletteColor(0x53a395, "Teal"),
                PaletteColor(0x4d884d, "Dark Green"),
                PaletteColor(0x66af59, "Bright Green"),
                PaletteColor(0xf277ac, "Hot Pink"),
                PaletteColor(0xeeb0c3, "Light Pink"),
                PaletteColor(0x7062a9, "Purple"),
                PaletteColor(0xea692b, "Orange"),
                PaletteColor(0xffd43a, "Yellow")
            ),
            "Warm Spring" to listOf(
                PaletteColor(0xe1c6b2, "Light Beige"),
                PaletteColor(0xad8e79, "Medium Tan"),
                PaletteColor(0x81624b, "Brown"),
                PaletteColor(0x56381b, "Dark Brown"),
                PaletteColor(0x98c5a8, "Mint"),
                PaletteColor(0x53a395, "Teal"),
                PaletteColor(0x4d884d, "Olive Green"),
                PaletteColor(0xc0c2e1, "Soft Lavender"),
                PaletteColor(0xe35069, "Coral Pink"),
                PaletteColor(0xea692b, "Orange"),
                PaletteColor(0xf1a02c, "Gold"),
                PaletteColor(0xffd43a, "Yellow")
            ),
            "Light Summer" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0xe1c6b2, "Light Beige"),
                PaletteColor(0xad8e79, "Medium Tan"),
                PaletteColor(0x9ea0a2, "Soft Gray"),
                PaletteColor(0x985f60, "Mauve"),
                PaletteColor(0x763751, "Burgundy"),
                PaletteColor(0xeeb0c3, "Light Pink"),
                PaletteColor(0xc0c2e1, "Soft Lavender"),
                PaletteColor(0x96b3e0, "Light Blue"),
                PaletteColor(0x3597c1, "Teal"),
                PaletteColor(0x365f8a, "Navy Blue"),
                PaletteColor(0x98c5a8, "Sage Green")
            ),
            "Soft Summer" to listOf(
                PaletteColor(0xffffff, "Pure White"),
                PaletteColor(0x9ea0a2, "Light Gray"),
                PaletteColor(0x6d6f73, "Dark Gray"),
                PaletteColor(0x442655, "Deep Purple"),
                PaletteColor(0x5d447a, "Navy Purple"),
                PaletteColor(0x750c1b, "Burgundy"),
                PaletteColor(0x763751, "Mauve"),
                PaletteColor(0x985f60, "Dusty Rose"),
                PaletteColor(0x365f8a, "Navy Blue"),
                PaletteColor(0xc0c2e1, "Soft Lavender"),
                PaletteColor(0x98c5a8, "Sage Green"),
                PaletteColor(0xfbe29f, "Light Yellow")
            ),
            "Cool Summer" to listOf(
                PaletteColor(0x9ea0a2, "Cool Gray"),
                PaletteColor(0x96b3e0, "Light Blue"),
                PaletteColor(0x3597c1, "Teal"),
                PaletteColor(0x365f8a, "Navy"),
                PaletteColor(0x1d1951, "Deep Navy"),
                PaletteColor(0x345a5d, "Dark Teal"),
                PaletteColor(0x98c5a8, "Sage Green"),
                PaletteColor(0xc0c2e1, "Soft Lavender"),
                PaletteColor(0x763751, "Mauve"),
                PaletteColor(0x442655, "Purple"),
                PaletteColor(0x750c1b, "Wine Red"),
                PaletteColor(0xa61f26, "Red")
            ),
            "Soft Autumn" to listOf(
                PaletteColor(0xe1c6b2, "Light Beige"),
                PaletteColor(0xad8e79, "Medium Tan"),
                PaletteColor(0x81624b, "Brown"),
                PaletteColor(0x985f60, "Mauve"),
                PaletteColor(0xb6542a, "Terracotta"),
                PaletteColor(0xf1a02c, "Gold"),
                PaletteColor(0xffd43a, "Yellow"),
                PaletteColor(0x53a395, "Teal"),
                PaletteColor(0x365f8a, "Navy Blue"),
                PaletteColor(0x345a5d, "Dark Teal"),
                PaletteColor(0x4d884d, "Olive Green"),
                PaletteColor(0x809353, "Sage Green")
            ),
            "Warm Autumn" to listOf(
                PaletteColor(0xe1c6b2, "Light Beige"),
                PaletteColor(0xad8e79, "Medium Tan"),
                PaletteColor(0x81624b, "Brown"),
                PaletteColor(0x56381b, "Dark Brown"),
                PaletteColor(0x68682f, "Olive"),
                PaletteColor(0x4d884d, "Forest Green"),
                PaletteColor(0x809353, "Sage Green"),
                PaletteColor(0xffd43a, "Yellow"),
                PaletteColor(0xf1a02c, "Gold"),
                PaletteColor(0xea692b, "Orange"),
                PaletteColor(0x750c1b, "Burgundy"),
                PaletteColor(0xa61f26, "Red")
            ),
            "Dark Autumn" to listOf(
                PaletteColor(0xad8e79, "Tan"),
                PaletteColor(0x56381b, "Dark Brown"),
                PaletteColor(0x68682f, "Olive Brown"),
                PaletteColor(0x2f5c38, "Dark Green"),
                PaletteColor(0x4d884d, "Forest Green"),
                PaletteColor(0x345a5d, "Dark Teal"),
                PaletteColor(0x750c1b, "Burgundy"),
                PaletteColor(0xa61f26, "Red"),
                PaletteColor(0xd9242c, "Bright Red"),
                PaletteColor(0xea692b, "Orange"),
                PaletteColor(0xb6542a, "Terracotta"),
                PaletteColor(0xf1a02c, "Gold")
            )
        )


        fun hexToRGB(hex: String): ColourRGB {
            val cleanHex = hex.removePrefix("#").removePrefix("0x")
            val value = cleanHex.toLong(16).toInt()
            return ColourRGB(
                r = (value shr 16) and 0xFF,
                g = (value shr 8) and 0xFF,
                b = value and 0xFF
            )
        }

        fun rgbToHex(r: Int, g: Int, b: Int): String {
            return String.format("%02x%02x%02x", r, g, b)
        }

        fun rgbToHSL(rgb: ColourRGB): ColourHSL {
            val r = rgb.r / 255.0
            val g = rgb.g / 255.0
            val b = rgb.b / 255.0

            val maxC = max(r, max(g, b))
            val minC = min(r, min(g, b))
            val delta = maxC - minC

            val l = (maxC + minC) / 2.0

            if (delta == 0.0) {
                return ColourHSL(0.0, 0.0, l)
            }

            val s = if (l < 0.5) delta / (maxC + minC) else delta / (2.0 - maxC - minC)

            val h = when (maxC) {
                r -> ((g - b) / delta + (if (g < b) 6 else 0)) / 6.0
                g -> ((b - r) / delta + 2) / 6.0
                else -> ((r - g) / delta + 4) / 6.0
            } * 360.0

            return ColourHSL(h, s, l)
        }

        fun analyzeUndertone(skinRGB: ColourRGB): String {
            val r = skinRGB.r
            val g = skinRGB.g
            val b = skinRGB.b

            val redVsBlue = r - b
            val warmScore = (r + g) - (2 * b)

            return when {
                warmScore > 50 -> "warm"
                warmScore < 0 -> "cool"
                redVsBlue < 25 && warmScore < 30 -> "cool"
                warmScore > 30 -> "warm"
                redVsBlue > 10 -> "warm-neutral"
                else -> "cool-neutral"
            }
        }

        fun analyzeValue(skinHSL: ColourHSL, hairHSL: ColourHSL): String {
            val avgLightness = (skinHSL.l + hairHSL.l) / 2.0
            val contrast = abs(skinHSL.l - hairHSL.l)

            if (hairHSL.l < 0.25 && contrast > 0.35) {
                return "dark"
            }

            return when {
                avgLightness > 0.65 -> "light"
                avgLightness < 0.35 -> "dark"
                else -> "medium"
            }
        }

        fun analyzeContrast(skinHSL: ColourHSL, hairHSL: ColourHSL, eyeHSL: ColourHSL?): String {
            val skinHairContrast = abs(skinHSL.l - hairHSL.l)
            val contrast = if (eyeHSL != null) {
                max(skinHairContrast, abs(skinHSL.l - eyeHSL.l))
            } else {
                skinHairContrast
            }

            return when {
                contrast > 0.35 -> "high"
                contrast < 0.2 -> "low"
                else -> "medium"
            }
        }

        fun analyzeChroma(skinHSL: ColourHSL, hairHSL: ColourHSL, eyeHSL: ColourHSL?): String {
            val avgSaturation = if (eyeHSL != null) {
                (skinHSL.s + hairHSL.s + eyeHSL.s) / 3.0
            } else {
                (skinHSL.s + hairHSL.s) / 2.0
            }

            return when {
                avgSaturation > 0.4 -> "bright"
                avgSaturation < 0.25 -> "muted"
                else -> "medium"
            }
        }

        suspend fun analyzeColours(extractedColours: ExtractedColours, personaId: Int): ColourAnalysisResult {
            println("=== STARTING COLOR ANALYSIS ===")
            println("Input ExtractedColours ID: ${extractedColours.id}")
            println("Forehead: ${extractedColours.foreheadHex}")
            println("Cheek: ${extractedColours.cheekHex}")
            println("Chin: ${extractedColours.chinHex}")
            println("Hair: ${extractedColours.hairHex}")
            println("Eye: ${extractedColours.eyeHex}")

            if (extractedColours.id == null) {
                throw IllegalStateException("ExtractedColours must have an ID before analysis")
            }

            val foreheadRGB = hexToRGB(extractedColours.foreheadHex)
            val cheekRGB = hexToRGB(extractedColours.cheekHex)
            val chinRGB = hexToRGB(extractedColours.chinHex)
            val hairRGB = hexToRGB(extractedColours.hairHex)
            val eyeRGB = extractedColours.eyeHex?.let { hexToRGB(it) }

            println("Forehead RGB: $foreheadRGB")
            println("Hair RGB: $hairRGB")

            val avgSkinRGB = ColourRGB(
                r = (foreheadRGB.r + cheekRGB.r + chinRGB.r) / 3,
                g = (foreheadRGB.g + cheekRGB.g + chinRGB.g) / 3,
                b = (foreheadRGB.b + cheekRGB.b + chinRGB.b) / 3
            )
            println("Average Skin RGB: $avgSkinRGB")

            val skinHSL = rgbToHSL(avgSkinRGB)
            val hairHSL = rgbToHSL(hairRGB)
            val eyeHSL = eyeRGB?.let { rgbToHSL(it) }

            println("Skin HSL: $skinHSL")
            println("Hair HSL: $hairHSL")
            println("Eye HSL: $eyeHSL")

            val undertone = analyzeUndertone(avgSkinRGB)
            val value = analyzeValue(skinHSL, hairHSL)
            val contrast = analyzeContrast(skinHSL, hairHSL, eyeHSL)
            val chroma = analyzeChroma(skinHSL, hairHSL, eyeHSL)

            println(">>> UNDERTONE: $undertone")
            println(">>> VALUE: $value")
            println(">>> CONTRAST: $contrast")
            println(">>> CHROMA: $chroma")

            val season = determineSeason(undertone, value, contrast, chroma)

            println(">>> FINAL SEASON: $season")

            // fetch the palette ID from the season name
            val palette = viewModel.fetch_palette_from_name(season)
            val paletteId = palette?.id ?: 1

            println(">>> PALETTE ID: $paletteId")
            println(">>> Using EXTRACTED COLOURS ID: ${extractedColours.id}")
            println("=== END COLOR ANALYSIS ===")

            val CAR_result = ColourAnalysisResult(
                id = null,
                undertone = undertone,
                value = value,
                contrast = contrast,
                chroma = chroma,
                extractedColours = extractedColours.id,
                palette = paletteId
            )
            val actualId = viewModel.add_CAR(CAR_result)
            println(">>> Saved ColourAnalysisResult with ID: $actualId")


            viewModel.update_persona_colour_analysis_result(personaId, actualId)
            println(">>> Linked ColourAnalysisResult $actualId to Persona $personaId")

            viewModel.model.populate_persona_recommendations(personaId)

            return CAR_result.copy(id = actualId)
        }

        fun determineSeason(
            undertone: String,
            value: String,
            contrast: String,
            chroma: String
        ): String {
            println("DEBUG - Undertone: $undertone, Value: $value, Contrast: $contrast, Chroma: $chroma")

            return when {
                // Spring (Warm + Light) -> most specific first
                undertone.contains("warm") && value == "light" && contrast == "high" && chroma == "bright" -> {
                    println("Matched: Bright Spring")
                    "Bright Spring"
                }
                undertone.contains("warm") && value == "light" && chroma == "bright" -> {
                    println("Matched: Warm Spring")
                    "Warm Spring"
                }
                undertone.contains("warm") && value == "light" -> {
                    println("Matched: Light Spring")
                    "Light Spring"
                }

                // Winter (Cool + High Contrast OR Dark)
                undertone.contains("cool") && value == "dark" && contrast == "high" && chroma == "bright" -> {
                    println("Matched: Bright Winter")
                    "Bright Winter"
                }
                undertone.contains("cool") && contrast == "high" && chroma == "bright" -> {
                    println("Matched: Bright Winter (alt)")
                    "Bright Winter"
                }
                undertone.contains("cool") && value == "dark" && contrast == "high" -> {
                    println("Matched: Dark Winter")
                    "Dark Winter"
                }
                undertone.contains("cool") && contrast == "high" -> {
                    println("Matched: Cool Winter")
                    "Cool Winter"
                }
                undertone.contains("cool") && value == "dark" -> {
                    println("Matched: Dark Winter (alt)")
                    "Dark Winter"
                }

                // Summer (Cool + Light/Medium + Lower Contrast)
                undertone.contains("cool") && value == "light" && chroma == "muted" && contrast == "low" -> {
                    println("Matched: Soft Summer")
                    "Soft Summer"
                }
                undertone.contains("cool") && value == "light" && chroma == "muted" -> {
                    println("Matched: Cool Summer")
                    "Cool Summer"
                }
                undertone.contains("cool") && value == "light" -> {
                    println("Matched: Light Summer")
                    "Light Summer"
                }
                undertone.contains("cool") && value == "medium" && chroma == "muted" -> {
                    println("Matched: Soft Summer (medium)")
                    "Soft Summer"
                }
                undertone.contains("cool") && value == "medium" -> {
                    println("Matched: Cool Summer (medium)")
                    "Cool Summer"
                }

                // autumn (Warm + Medium/Dark)
                undertone.contains("warm") && value == "dark" && contrast == "high" && chroma == "muted" -> {
                    println("Matched: Dark Autumn (specific)")
                    "Dark Autumn"
                }
                undertone.contains("warm") && value == "dark" && contrast == "high" -> {
                    println("Matched: Dark Autumn")
                    "Dark Autumn"
                }
                undertone.contains("warm") && value == "medium" && chroma == "muted" -> {  // Changed from != "light"
                    println("Matched: Soft Autumn")
                    "Soft Autumn"
                }
                undertone.contains("warm") && value == "dark" -> {
                    println("Matched: Dark Autumn (value)")
                    "Dark Autumn"
                }
                undertone.contains("warm") && value == "medium" -> {
                    println("Matched: Warm Autumn")
                    "Warm Autumn"
                }
                undertone.contains("warm") -> {
                    println("Matched: Warm Autumn (default warm)")
                    "Warm Autumn"
                }

                // neutral undertones
                undertone.contains("neutral") && value == "light" -> {
                    println("Matched: Light Summer (neutral)")
                    "Light Summer"
                }
                undertone.contains("neutral") && chroma == "muted" -> {
                    println("Matched: Soft Summer (neutral)")
                    "Soft Summer"
                }

                // Fallback
                else -> {
                    println("Matched: Cool Summer (fallback)")
                    "Cool Summer"
                }
            }

        }
    }

        // extract hex codes from face capture image
        inner class ImageColourExtractor {

            suspend fun extractColoursFromFace(image: BufferedImage): ExtractedColours {
                val width = image.width
                val height = image.height

                val foreheadHex = extractRegionColour(
                    image,
                    xStart = (width * 0.30).toInt(),
                    xEnd = (width * 0.70).toInt(),
                    yStart = (height * 0.20).toInt(),
                    yEnd = (height * 0.35).toInt()
                )

                val leftCheekHex = extractRegionColour(
                    image,
                    xStart = (width * 0.20).toInt(),
                    xEnd = (width * 0.40).toInt(),
                    yStart = (height * 0.40).toInt(),
                    yEnd = (height * 0.55).toInt()
                )

                val rightCheekHex = extractRegionColour(
                    image,
                    xStart = (width * 0.60).toInt(),
                    xEnd = (width * 0.80).toInt(),
                    yStart = (height * 0.40).toInt(),
                    yEnd = (height * 0.55).toInt()
                )

                val cheekHex = averageHexColours(leftCheekHex, rightCheekHex)

                val chinHex = extractRegionColour(
                    image,
                    xStart = (width * 0.30).toInt(),
                    xEnd = (width * 0.70).toInt(),
                    yStart = (height * 0.60).toInt(),
                    yEnd = (height * 0.70).toInt()
                )

                val hairHex = extractRegionColour(
                    image,
                    xStart = (width * 0.20).toInt(),
                    xEnd = (width * 0.80).toInt(),
                    yStart = (height * 0.05).toInt(),
                    yEnd = (height * 0.20).toInt()
                )

                try {
                } catch (e: Exception) {
                    // finish after
                }
                val eyeHex = try {
                    extractRegionColour(
                        image,
                        xStart = (width * 0.35).toInt(),
                        xEnd = (width * 0.65).toInt(),
                        yStart = (height * 0.35).toInt(),
                        yEnd = (height * 0.40).toInt()
                    )
                } catch (e: Exception) {
                    null
                }

                val EC_result = ExtractedColours(
                    id = null,
                    foreheadHex = foreheadHex,
                    cheekHex = cheekHex,
                    chinHex = chinHex,
                    hairHex = hairHex,
                    eyeHex = eyeHex
                )
                val actualId = viewModel.add_extract_colours(EC_result)
                return EC_result.copy(id = actualId)
            }


            fun extractRegionColour(
                image: BufferedImage,
                xStart: Int,
                xEnd: Int,
                yStart: Int,
                yEnd: Int
            ): String {
                var totalR = 0L
                var totalG = 0L
                var totalB = 0L
                var pixelCount = 0

                val safeXStart = max(0, xStart)
                val safeXEnd = min(image.width, xEnd)
                val safeYStart = max(0, yStart)
                val safeYEnd = min(image.height, yEnd)

                val sampleRate = 3

                for (y in safeYStart until safeYEnd step sampleRate) {
                    for (x in safeXStart until safeXEnd step sampleRate) {
                        val rgb = image.getRGB(x, y)
                        val r = (rgb shr 16) and 0xFF
                        val g = (rgb shr 8) and 0xFF
                        val b = rgb and 0xFF

                        val brightness = (r + g + b) / 3
                        if (brightness in 30..240) {
                            totalR += r
                            totalG += g
                            totalB += b
                            pixelCount++
                        }
                    }
                }

                if (pixelCount == 0) {
                    return "000000"
                }

                val avgR = (totalR / pixelCount).toInt()
                val avgG = (totalG / pixelCount).toInt()
                val avgB = (totalB / pixelCount).toInt()

                return ColourAnalysisService().rgbToHex(avgR, avgG, avgB)
            }


            fun averageHexColours(hex1: String, hex2: String): String {
                val rgb1 = ColourAnalysisService().hexToRGB(hex1)
                val rgb2 = ColourAnalysisService().hexToRGB(hex2)

                val avgR = (rgb1.r + rgb2.r) / 2
                val avgG = (rgb1.g + rgb2.g) / 2
                val avgB = (rgb1.b + rgb2.b) / 2

                return ColourAnalysisService().rgbToHex(avgR, avgG, avgB)
            }


            suspend fun extractColoursWithRegions(
                image: BufferedImage,
                foreheadRegion: Region? = null,
                cheekRegion: Region? = null,
                chinRegion: Region? = null,
                hairRegion: Region? = null
            ): ExtractedColours {
                val width = image.width
                val height = image.height

                val foreheadHex = foreheadRegion?.let {
                    extractRegionColour(image, it.xStart, it.xEnd, it.yStart, it.yEnd)
                } ?: extractRegionColour(
                    image,
                    (width * 0.30).toInt(),
                    (width * 0.70).toInt(),
                    (height * 0.20).toInt(),
                    (height * 0.35).toInt()
                )

                val cheekHex = cheekRegion?.let {
                    extractRegionColour(image, it.xStart, it.xEnd, it.yStart, it.yEnd)
                } ?: run {
                    val leftCheek = extractRegionColour(
                        image,
                        (width * 0.20).toInt(),
                        (width * 0.40).toInt(),
                        (height * 0.40).toInt(),
                        (height * 0.55).toInt()
                    )
                    val rightCheek = extractRegionColour(
                        image,
                        (width * 0.60).toInt(),
                        (width * 0.80).toInt(),
                        (height * 0.40).toInt(),
                        (height * 0.55).toInt()
                    )
                    averageHexColours(leftCheek, rightCheek)
                }

                val chinHex = chinRegion?.let {
                    extractRegionColour(image, it.xStart, it.xEnd, it.yStart, it.yEnd)
                } ?: extractRegionColour(
                    image,
                    (width * 0.30).toInt(),
                    (width * 0.70).toInt(),
                    (height * 0.60).toInt(),
                    (height * 0.70).toInt()
                )

                val hairHex = hairRegion?.let {
                    extractRegionColour(image, it.xStart, it.xEnd, it.yStart, it.yEnd)
                } ?: extractRegionColour(
                    image,
                    (width * 0.20).toInt(),
                    (width * 0.80).toInt(),
                    (height * 0.05).toInt(),
                    (height * 0.20).toInt()
                )

                val EC_result = ExtractedColours(
                    id = null,
                    foreheadHex = foreheadHex,
                    cheekHex = cheekHex,
                    chinHex = chinHex,
                    hairHex = hairHex,
                    eyeHex = null
                )
                val actualId = viewModel.add_extract_colours(EC_result)
                return EC_result.copy(id = actualId)
            }


        }
    }
