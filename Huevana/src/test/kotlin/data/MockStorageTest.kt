package org.example.data

import domain.Persona
import domain.Product
import domain.User
import kotlinx.coroutines.runBlocking
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.HexCode
import org.example.domain.Palette
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MockStorageTest {

    private fun sampleUser() = User(
        id = 0,
        username = "vkallam",
        password = "password",
        email = "kallamvinita@gmail.com",
        firstname = "Vinita",
        lastname = "Kallam",
        defaultPersona = null
    )

    @Test
    fun `sign up creates new user`() = runBlocking {
        val storage = MockStorage()
        val newId = storage.signUp_new_user("user@example.com", "secret")

        val stored = storage.fetch_user_from_id(newId)
        assertEquals("user@example.com", stored.email)
        assertTrue(storage.usersSnapshot().isNotEmpty())
    }

    @Test
    fun `sign up prevents duplicates`() = runBlocking {
        val storage = MockStorage()
        storage.signUp_new_user("user@example.com", "secret")

        val ex = assertFailsWith<IllegalStateException> {
            storage.signUp_new_user("user@example.com", "another")
        }
        assertEquals("user_already_exists", ex.message)
    }

    @Test
    fun `add user updates existing`() = runBlocking {
        val storage = MockStorage()
        storage.signUp_new_user("kallamvinita@gmail.com", "oldpass")

        val userId = storage.add_user(sampleUser())
        val stored = storage.fetch_user_from_id(userId)

        assertEquals("vkallam", stored.username)
        assertEquals("Vinita", stored.firstname)
    }

    @Test
    fun `user list modifications`() = runBlocking {
        val storage = MockStorage()
        val userId = storage.add_user(sampleUser())

        storage.add_item_id_to_user_list(userId, 2, "friends")
        storage.add_item_id_to_user_list(userId, 5, "wishList")
        storage.update_user_column_string(userId, "firstname", "Updated")
        storage.update_user_default_persona(userId, 10)

        val stored = storage.fetch_user_from_id(userId)
        assertEquals(listOf(2), stored.friends)
        assertEquals(listOf(5), stored.wishList)
        assertEquals("Updated", stored.firstname)
        assertEquals(10, stored.defaultPersona)
    }

    @Test
    fun `persona operations`() = runBlocking {
        val storage = MockStorage()
        val personaId = storage.add_persona(
            Persona(
                id = 0,
                name = "Original",
                recommendedProducts = mutableListOf(),
                colourAnalysisResult = null
            )
        )

        storage.add_item_id_to_persona_list(personaId, 1, "recommendedProducts", 1)
        storage.update_persona_name(personaId, "Updated Persona", 1)
        storage.update_persona_colour_analysis_result(personaId, 7, 1)

        val persona = storage.fetch_persona_from_id(personaId)
        assertEquals(listOf(1), persona.recommendedProducts?.toList())
        assertEquals("Updated Persona", persona.name)
        assertEquals(7, persona.colourAnalysisResult)
    }

    @Test
    fun `colour analysis and extracted colours`() = runBlocking {
        val storage = MockStorage()
        val extractId = storage.add_extract_colours(
            ExtractedColours(
                id = 0,
                foreheadHex = "AAA111",
                cheekHex = "BBB222",
                chinHex = "CCC333",
                hairHex = "DDD444",
                eyeHex = null
            )
        ) ?: error("Failed to add extracted colours")

        storage.update_extract_colours_column_string(extractId, "eyeHex", "EEE555")

        val carId = storage.add_CAR(
            ColourAnalysisResult(
                id = 0,
                undertone = "Warm",
                value = "Medium",
                contrast = "High",
                chroma = "Bright",
                extractedColours = extractId,
                palette = 1
            )
        ) ?: error("Failed to add colour analysis result")

        storage.update_CAR_column_string(carId, "undertone", "Cool")
        storage.update_CAR_column_int(carId, "palette", 9)

        val car = storage.fetch_CAR_from_id(carId)
        val colours = storage.fetch_extract_colours_from_id(extractId)

        assertEquals("Cool", car.undertone)
        assertEquals(9, car.palette)
        assertEquals("EEE555", colours.eyeHex)
    }

    @Test
    fun `seeding and fetching other tables`() = runBlocking {
        val storage = MockStorage()
        storage.seedHexCode(HexCode(id = 1L, hexCode = "FFFFFF"))
        storage.seedPalette(
            Palette(
                id = 1,
                name = "Spring",
                hexCodes = mutableListOf("FFFFFF"),
                products = mutableListOf(1)
            )
        )
        storage.seedProduct(
            Product(
                id = 1,
                name = "Lipstick",
                brand = "Huevana",
                imageUrl = "https://example.com/lipstick.jpg",
                palette = 1,
                rating = 5.0f,
                shade = "Red"
            )
        )

        val hex = storage.fetch_hex_code_from_id(1)
        val palette = storage.fetch_palette_from_id(1)
        val product = storage.fetch_product_from_id(1)

        assertEquals("FFFFFF", hex.hexCode)
        assertEquals("Spring", palette.name)
        assertEquals("Lipstick", product.name)
    }
}
