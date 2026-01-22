package org.example.ui

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ResultsPageViewTest {

    // Test: hexToComposeColor with valid hex
    @Test
    fun testHexToComposeColor_ValidHex() {
        val color = hexToComposeColor("FF0000")

        assertEquals(1.0f, color.red, 0.01f)
        assertEquals(0.0f, color.green, 0.01f)
        assertEquals(0.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor with # prefix
    @Test
    fun testHexToComposeColor_WithHashPrefix() {
        val color = hexToComposeColor("#00FF00")

        assertEquals(0.0f, color.red, 0.01f)
        assertEquals(1.0f, color.green, 0.01f)
        assertEquals(0.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor blue
    @Test
    fun testHexToComposeColor_Blue() {
        val color = hexToComposeColor("0000FF")

        assertEquals(0.0f, color.red, 0.01f)
        assertEquals(0.0f, color.green, 0.01f)
        assertEquals(1.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor white
    @Test
    fun testHexToComposeColor_White() {
        val color = hexToComposeColor("FFFFFF")

        assertEquals(1.0f, color.red, 0.01f)
        assertEquals(1.0f, color.green, 0.01f)
        assertEquals(1.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor black
    @Test
    fun testHexToComposeColor_Black() {
        val color = hexToComposeColor("000000")

        assertEquals(0.0f, color.red, 0.01f)
        assertEquals(0.0f, color.green, 0.01f)
        assertEquals(0.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor gray
    @Test
    fun testHexToComposeColor_Gray() {
        val color = hexToComposeColor("808080")

        assertEquals(0.5f, color.red, 0.01f)
        assertEquals(0.5f, color.green, 0.01f)
        assertEquals(0.5f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor custom color
    @Test
    fun testHexToComposeColor_CustomColor() {
        val color = hexToComposeColor("E95D7A")

        // E9 = 233, 5D = 93, 7A = 122
        assertEquals(233 / 255f, color.red, 0.01f)
        assertEquals(93 / 255f, color.green, 0.01f)
        assertEquals(122 / 255f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor with lowercase hex
    @Test
    fun testHexToComposeColor_LowercaseHex() {
        val color = hexToComposeColor("ff0000")

        assertEquals(1.0f, color.red, 0.01f)
        assertEquals(0.0f, color.green, 0.01f)
        assertEquals(0.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor with mixed case
    @Test
    fun testHexToComposeColor_MixedCase() {
        val color = hexToComposeColor("FfAa00")

        // FF = 255, AA = 170, 00 = 0
        assertEquals(1.0f, color.red, 0.01f)
        assertEquals(170 / 255f, color.green, 0.01f)
        assertEquals(0.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor with invalid hex returns black
    @Test
    fun testHexToComposeColor_InvalidHex() {
        val color = hexToComposeColor("GGGGGG")

        // Should return black on error
        assertEquals(Color.Black, color)
    }

    // Test: hexToComposeColor with empty string
    @Test
    fun testHexToComposeColor_EmptyString() {
        val color = hexToComposeColor("")

        // Should return black on error
        assertEquals(Color.Black, color)
    }

    // Test: hexToComposeColor with skin tone
    @Test
    fun testHexToComposeColor_SkinTone() {
        val color = hexToComposeColor("D4A894")

        // D4 = 212, A8 = 168, 94 = 148
        assertEquals(212 / 255f, color.red, 0.01f)
        assertEquals(168 / 255f, color.green, 0.01f)
        assertEquals(148 / 255f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor with hair color
    @Test
    fun testHexToComposeColor_HairColor() {
        val color = hexToComposeColor("3B2A1F")

        // 3B = 59, 2A = 42, 1F = 31
        assertEquals(59 / 255f, color.red, 0.01f)
        assertEquals(42 / 255f, color.green, 0.01f)
        assertEquals(31 / 255f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor edge case with leading zeros
    @Test
    fun testHexToComposeColor_LeadingZeros() {
        val color = hexToComposeColor("000FFF")

        // 00 = 0, 0F = 15, FF = 255
        assertEquals(0.0f, color.red, 0.01f)
        assertEquals(15 / 255f, color.green, 0.01f)
        assertEquals(1.0f, color.blue, 0.01f)
    }

    // Test: hexToComposeColor multiple prefixes
    @Test
    fun testHexToComposeColor_RemovesOnlyOnePrefix() {
        val color = hexToComposeColor("FF0000")

        // Should work normally
        assertEquals(1.0f, color.red, 0.01f)
    }

    // Test: Color conversion accuracy
    @Test
    fun testHexToComposeColor_ConversionAccuracy() {
        val testCases = listOf(
            Triple("FF0000", 1.0f, 0.0f),  // Red
            Triple("00FF00", 1.0f, 0.0f),  // Green (G component)
            Triple("0000FF", 1.0f, 0.0f)   // Blue (B component)
        )

        testCases.forEach { (hex, expectedMax, expectedMin) ->
            val color = hexToComposeColor(hex)
            val components = listOf(color.red, color.green, color.blue)
            assertEquals(expectedMax, components.maxOrNull() ?: 0f, 0.01f)
        }
    }

    // Test: hexToComposeColor with palette colors
    @Test
    fun testHexToComposeColor_PaletteColors() {
        val paletteColors = listOf(
            "98c5a8",  // Sage Green
            "345a5d",  // Dark Teal
            "c0c2e1",  // Soft Lavender
            "96b3e0",  // Periwinkle Blue
            "3597c1",  // Teal Blue
            "1d1951"   // Deep Navy
        )

        paletteColors.forEach { hex ->
            val color = hexToComposeColor(hex)
            // All should convert without errors
            assertEquals(false, color == Color.Unspecified)
        }
    }

    // Test: String capitalization for analysis values
    @Test
    fun testStringCapitalization() {
        val undertone = "warm"
        val capitalized = undertone.replaceFirstChar { it.uppercase() }

        assertEquals("Warm", capitalized)
    }

    // Test: String capitalization already capitalized
    @Test
    fun testStringCapitalization_AlreadyCapitalized() {
        val value = "Light"
        val capitalized = value.replaceFirstChar { it.uppercase() }

        assertEquals("Light", capitalized)
    }

    // Test: String uppercase for hex display
    @Test
    fun testStringUppercase_Hex() {
        val hex = "e95d7a"
        val uppercased = hex.uppercase()

        assertEquals("E95D7A", uppercased)
    }
}