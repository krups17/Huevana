package org.example.domain

import domain.Model
import org.example.data.MockStorage
import org.example.presentation.FaceCaptureViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val faceCaptureViewModel = FaceCaptureViewModel(Model(MockStorage()))
private val colourAnalysis = ColourAnalysis(faceCaptureViewModel)
private val colourAnalysisService = colourAnalysis.ColourAnalysisService()

private typealias ColorRGB = ColourAnalysis.ColourRGB
private typealias ColorHSL = ColourAnalysis.ColourHSL

private fun hexToRGB(hex: String) = colourAnalysisService.hexToRGB(hex)
private fun rgbToHex(r: Int, g: Int, b: Int) = colourAnalysisService.rgbToHex(r, g, b)
private fun rgbToHSL(rgb: ColorRGB) = colourAnalysisService.rgbToHSL(rgb)
private fun analyzeUndertone(rgb: ColorRGB) = colourAnalysisService.analyzeUndertone(rgb)
private fun analyzeValue(skin: ColorHSL, hair: ColorHSL) = colourAnalysisService.analyzeValue(skin, hair)
private fun analyzeContrast(skin: ColorHSL, hair: ColorHSL, eye: ColorHSL?) =
    colourAnalysisService.analyzeContrast(skin, hair, eye)
private fun analyzeChroma(skin: ColorHSL, hair: ColorHSL, eye: ColorHSL?) =
    colourAnalysisService.analyzeChroma(skin, hair, eye)
private fun determineSeason(undertone: String, value: String, contrast: String, chroma: String) =
    colourAnalysisService.determineSeason(undertone, value, contrast, chroma)

class ColourAnalysisTest {

    // Test: hexToRGB conversion with 0x prefix
    @Test
    fun testHexToRGB_WithPrefix0x() {
        val result = hexToRGB("0x48b847")
        assertEquals(72, result.r)
        assertEquals(184, result.g)
        assertEquals(71, result.b)
    }

    // Test: hexToRGB without prefix
    @Test
    fun testHexToRGB_WithoutPrefix() {
        val result = hexToRGB("fab415")
        assertEquals(250, result.r)
        assertEquals(180, result.g)
        assertEquals(21, result.b)
    }

    // Test: hexToRGB with # prefix
    @Test
    fun testHexToRGB_WithHashPrefix() {
        val result = hexToRGB("#f1354d")
        assertEquals(241, result.r)
        assertEquals(53, result.g)
        assertEquals(77, result.b)
    }

    // Test: hexToRGB black
    @Test
    fun testHexToRGB_Black() {
        val result = hexToRGB("000000")
        assertEquals(0, result.r)
        assertEquals(0, result.g)
        assertEquals(0, result.b)
    }

    // Test: hexToRGB white
    @Test
    fun testHexToRGB_White() {
        val result = hexToRGB("ffffff")
        assertEquals(255, result.r)
        assertEquals(255, result.g)
        assertEquals(255, result.b)
    }

    // Test: rgbToHex conversion
    @Test
    fun testRgbToHex() {
        val result = rgbToHex(72, 184, 71)
        assertEquals("48b847", result)
    }

    // Test: rgbToHex black
    @Test
    fun testRgbToHex_Black() {
        val result = rgbToHex(0, 0, 0)
        assertEquals("000000", result)
    }

    // Test: rgbToHex white
    @Test
    fun testRgbToHex_White() {
        val result = rgbToHex(255, 255, 255)
        assertEquals("ffffff", result)
    }

    // Test: RGB to HSL conversion red
    @Test
    fun testRgbToHSL_Red() {
        val rgb = ColorRGB(255, 0, 0)
        val hsl = rgbToHSL(rgb)
        assertEquals(0.0, hsl.h, 0.1)
        assertEquals(1.0, hsl.s, 0.01)
        assertEquals(0.5, hsl.l, 0.01)
    }

    // Test: RGB to HSL conversion green
    @Test
    fun testRgbToHSL_Green() {
        val rgb = ColorRGB(0, 255, 0)
        val hsl = rgbToHSL(rgb)
        assertEquals(120.0, hsl.h, 1.0)
        assertEquals(1.0, hsl.s, 0.01)
        assertEquals(0.5, hsl.l, 0.01)
    }

    // Test: RGB to HSL conversion blue
    @Test
    fun testRgbToHSL_Blue() {
        val rgb = ColorRGB(0, 0, 255)
        val hsl = rgbToHSL(rgb)
        assertEquals(240.0, hsl.h, 1.0)
        assertEquals(1.0, hsl.s, 0.01)
        assertEquals(0.5, hsl.l, 0.01)
    }

    // Test: RGB to HSL conversion gray
    @Test
    fun testRgbToHSL_Gray() {
        val rgb = ColorRGB(128, 128, 128)
        val hsl = rgbToHSL(rgb)
        assertEquals(0.0, hsl.s, 0.01)
        assertEquals(0.502, hsl.l, 0.01)
    }

    // Test: RGB to HSL conversion black
    @Test
    fun testRgbToHSL_Black() {
        val rgb = ColorRGB(0, 0, 0)
        val hsl = rgbToHSL(rgb)
        assertEquals(0.0, hsl.h)
        assertEquals(0.0, hsl.s)
        assertEquals(0.0, hsl.l)
    }

    // Test: Undertone warm
    @Test
    fun testAnalyzeUndertone_Warm() {
        val warmSkin = ColorRGB(210, 180, 140)
        val result = analyzeUndertone(warmSkin)
        assertTrue(result.contains("warm"))
    }

    // Test: Undertone cool
    @Test
    fun testAnalyzeUndertone_Cool() {
        val coolSkin = ColorRGB(180, 160, 180)
        val result = analyzeUndertone(coolSkin)
        assertTrue(result.contains("cool"))
    }

    // Test: Undertone very warm
    @Test
    fun testAnalyzeUndertone_VeryWarm() {
        val veryWarmSkin = ColorRGB(220, 180, 140)
        assertEquals("warm", analyzeUndertone(veryWarmSkin))
    }

    // Test: Undertone very cool
    @Test
    fun testAnalyzeUndertone_VeryCool() {
        val veryCoolSkin = ColorRGB(180, 170, 200)
        assertEquals("cool", analyzeUndertone(veryCoolSkin))
    }

    // Test: Value light
    @Test
    fun testAnalyzeValue_Light() {
        val lightSkin = ColorHSL(30.0, 0.3, 0.75)
        val lightHair = ColorHSL(50.0, 0.5, 0.7)
        assertEquals("light", analyzeValue(lightSkin, lightHair))
    }

    // Test: Value dark
    @Test
    fun testAnalyzeValue_Dark() {
        val darkSkin = ColorHSL(30.0, 0.3, 0.25)
        val darkHair = ColorHSL(20.0, 0.2, 0.15)
        assertEquals("dark", analyzeValue(darkSkin, darkHair))
    }

    // Test: Value medium
    @Test
    fun testAnalyzeValue_Medium() {
        val mediumSkin = ColorHSL(30.0, 0.3, 0.5)
        val mediumHair = ColorHSL(25.0, 0.4, 0.45)
        assertEquals("medium", analyzeValue(mediumSkin, mediumHair))
    }

    // Test: Value boundary case light
    @Test
    fun testAnalyzeValue_BoundaryCaseLight() {
        val skin = ColorHSL(0.0, 0.0, 0.66)
        val hair = ColorHSL(0.0, 0.0, 0.65)
        assertEquals("light", analyzeValue(skin, hair))
    }

    // Test: Value boundary case dark
    @Test
    fun testAnalyzeValue_BoundaryCaseDark() {
        val skin = ColorHSL(0.0, 0.0, 0.34)
        val hair = ColorHSL(0.0, 0.0, 0.35)
        assertEquals("dark", analyzeValue(skin, hair))
    }

    // Test: Contrast high
    @Test
    fun testAnalyzeContrast_High() {
        val lightSkin = ColorHSL(0.0, 0.2, 0.8)
        val darkHair = ColorHSL(0.0, 0.1, 0.2)
        val result = analyzeContrast(lightSkin, darkHair, null)
        assertEquals("high", result)
    }

    // Test: Contrast low
    @Test
    fun testAnalyzeContrast_Low() {
        val skin = ColorHSL(0.0, 0.2, 0.5)
        val hair = ColorHSL(0.0, 0.15, 0.52)
        val result = analyzeContrast(skin, hair, null)
        assertEquals("low", result)
    }

    // Test: Contrast medium
    @Test
    fun testAnalyzeContrast_Medium() {
        val skin = ColorHSL(0.0, 0.2, 0.6)
        val hair = ColorHSL(0.0, 0.15, 0.35)
        val result = analyzeContrast(skin, hair, null)
        assertEquals("medium", result)
    }

    // Test: Contrast with eye color
    @Test
    fun testAnalyzeContrast_WithEyeColor() {
        val skin = ColorHSL(0.0, 0.2, 0.7)
        val hair = ColorHSL(0.0, 0.1, 0.3)
        val eyes = ColorHSL(200.0, 0.4, 0.4)
        val result = analyzeContrast(skin, hair, eyes)
        assertEquals("high", result)
    }

    // Test: Chroma bright
    @Test
    fun testAnalyzeChroma_Bright() {
        val skin = ColorHSL(30.0, 0.5, 0.5)
        val hair = ColorHSL(40.0, 0.6, 0.4)
        val result = analyzeChroma(skin, hair, null)
        assertEquals("bright", result)
    }

    // Test: Chroma muted
    @Test
    fun testAnalyzeChroma_Muted() {
        val skin = ColorHSL(30.0, 0.15, 0.5)
        val hair = ColorHSL(40.0, 0.2, 0.4)
        val result = analyzeChroma(skin, hair, null)
        assertEquals("muted", result)
    }

    // Test: Chroma medium
    @Test
    fun testAnalyzeChroma_Medium() {
        val skin = ColorHSL(30.0, 0.3, 0.5)
        val hair = ColorHSL(40.0, 0.35, 0.4)
        val result = analyzeChroma(skin, hair, null)
        assertEquals("medium", result)
    }

    // Test: Chroma with eye color
    @Test
    fun testAnalyzeChroma_WithEyeColor() {
        val skin = ColorHSL(30.0, 0.4, 0.5)
        val hair = ColorHSL(40.0, 0.5, 0.4)
        val eyes = ColorHSL(200.0, 0.6, 0.3)
        val result = analyzeChroma(skin, hair, eyes)
        assertEquals("bright", result)
    }

    // Test: Season Bright Spring
    @Test
    fun testDetermineSeason_BrightSpring() {
        val result = determineSeason("warm", "light", "high", "bright")
        assertEquals("Bright Spring", result)
    }

    // Test: Season Light Spring
    @Test
    fun testDetermineSeason_LightSpring() {
        val result = determineSeason("warm", "light", "low", "muted")
        assertEquals("Light Spring", result)
    }

    // Test: Season Cool Summer
    @Test
    fun testDetermineSeason_CoolSummer() {
        val result = determineSeason("cool", "light", "medium", "muted")
        assertEquals("Cool Summer", result)
    }

    // Test: Season Light Summer
    @Test
    fun testDetermineSeason_LightSummer() {
        val result = determineSeason("cool", "light", "low", "medium")
        assertEquals("Light Summer", result)
    }

    // Test: Season Soft Summer
    @Test
    fun testDetermineSeason_SoftSummer() {
        val result = determineSeason("cool", "light", "low", "muted")
        assertEquals("Soft Summer", result)
    }

    // Test: Season Warm Autumn
    @Test
    fun testDetermineSeason_WarmAutumn() {
        val result = determineSeason("warm", "medium", "medium", "medium")
        assertEquals("Warm Autumn", result)
    }

    // Test: Season Soft Autumn
    @Test
    fun testDetermineSeason_SoftAutumn() {
        val result = determineSeason("warm", "medium", "low", "muted")
        assertEquals("Soft Autumn", result)
    }

    // Test: Season Dark Autumn
    @Test
    fun testDetermineSeason_DarkAutumn() {
        val result = determineSeason("warm", "dark", "high", "medium")
        assertEquals("Dark Autumn", result)
    }

    // Test: Season Bright Winter
    @Test
    fun testDetermineSeason_BrightWinter() {
        val result = determineSeason("cool", "medium", "high", "bright")
        assertEquals("Bright Winter", result)
    }

    // Test: Season Cool Winter
    @Test
    fun testDetermineSeason_CoolWinter() {
        val result = determineSeason("cool", "dark", "high", "bright")
        assertEquals("Bright Winter", result)
    }

    // Test: Season Dark Winter
    @Test
    fun testDetermineSeason_DarkWinter() {
        val result = determineSeason("cool", "dark", "medium", "medium")
        assertEquals("Dark Winter", result)
    }

    // Test: Season fallback warm
    @Test
    fun testDetermineSeason_FallbackWarm() {
        val result = determineSeason("warm-neutral", "medium", "medium", "medium")
        assertEquals("Warm Autumn", result)
    }

    // Test: Season fallback cool
    @Test
    fun testDetermineSeason_FallbackCool() {
        val result = determineSeason("cool-neutral", "medium", "medium", "medium")
        assertEquals("Cool Summer", result)
    }

    // Test: Full analysis Bright Spring example
    @Test
    fun testFullAnalysis_BrightSpringExample() {
        val skinRGB = hexToRGB("f5d5c0")
        val hairRGB = hexToRGB("e8d9a8")
        val eyeRGB = hexToRGB("4a90e2")

        val skinHSL = rgbToHSL(skinRGB)
        val hairHSL = rgbToHSL(hairRGB)
        val eyeHSL = rgbToHSL(eyeRGB)

        val undertone = analyzeUndertone(skinRGB)
        val value = analyzeValue(skinHSL, hairHSL)
        val contrast = analyzeContrast(skinHSL, hairHSL, eyeHSL)
        val chroma = analyzeChroma(skinHSL, hairHSL, eyeHSL)
        val season = determineSeason(undertone, value, contrast, chroma)

        assertTrue(season.contains("Spring"))
    }

    // Test: Full analysis Dark Winter example
    @Test
    fun testFullAnalysis_DarkWinterExample() {
        val skinRGB = hexToRGB("e8d5d0")
        val hairRGB = hexToRGB("2a1f1f")
        val eyeRGB = hexToRGB("3d2f2f")

        val skinHSL = rgbToHSL(skinRGB)
        val hairHSL = rgbToHSL(hairRGB)
        val eyeHSL = rgbToHSL(eyeRGB)

        val undertone = analyzeUndertone(skinRGB)
        val value = analyzeValue(skinHSL, hairHSL)
        val contrast = analyzeContrast(skinHSL, hairHSL, eyeHSL)
        val chroma = analyzeChroma(skinHSL, hairHSL, eyeHSL)
        val season = determineSeason(undertone, value, contrast, chroma)

        assertTrue(season.contains("Winter"))
    }

    // Test: Hex to RGB to Hex
    @Test
    fun testHexToRGBToHexRoundTrip() {
        val originalHex = "48b847"
        val rgb = hexToRGB(originalHex)
        val resultHex = rgbToHex(rgb.r, rgb.g, rgb.b)
        assertEquals(originalHex, resultHex)
    }

    // Test: RGB to HSL to analyze undertone warm
    @Test
    fun testRGBToHSLToUndertoneWarm() {
        val warmRGB = ColorRGB(220, 180, 140)
        val result = analyzeUndertone(warmRGB)
        assertEquals("warm", result)
    }

    // Test: RGB to HSL to analyze undertone cool
    @Test
    fun testRGBToHSLToUndertoneCool() {
        val coolRGB = ColorRGB(180, 170, 200)
        val result = analyzeUndertone(coolRGB)
        assertEquals("cool", result)
    }

    // Test: Value with high contrast dark hair
    @Test
    fun testAnalyzeValue_DarkHairHighContrast() {
        val lightSkin = ColorHSL(30.0, 0.3, 0.7)
        val darkHair = ColorHSL(20.0, 0.2, 0.2)
        assertEquals("dark", analyzeValue(lightSkin, darkHair))
    }
}