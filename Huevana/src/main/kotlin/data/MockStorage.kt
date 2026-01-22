package org.example.data

import domain.Persona
import domain.Product
import domain.User
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.FESharedPersonas
import org.example.domain.HexCode
import org.example.domain.Palette
import org.example.domain.SharedPersonas

/**
 * Simple in-memory implementation of [IStorage] for unit tests.
 * Each collection is backed by a mutable list and ids are generated locally.
 */
class MockStorage : IStorage {

    private var userIdCounter = 1
    private var personaIdCounter = 1
    private var sharedPersonaCounter = 1
    private var paletteIdCounter = 1
    private var productIdCounter = 1
    private var hexCodeIdCounter = 1L
    private var extractedColoursIdCounter = 1
    private var colourAnalysisResultIdCounter = 1

    private val users = mutableListOf<User>()
    private val personas = mutableListOf<Persona>()
    val palettes = mutableListOf<Palette>()
    val products = mutableListOf<Product>()
    private val hexCodes = mutableListOf<HexCode>()
    private val extractedColours = mutableListOf<ExtractedColours>()
    val colourAnalysisResults = mutableListOf<ColourAnalysisResult>()
    private val sharedPersonas = mutableListOf<SharedPersonas>()
    private val FESharedPersonas = mutableListOf<FESharedPersonas>()

    fun seedHexCode(hexCode: HexCode) {
        hexCodes.removeIf { it.id == hexCode.id }
        hexCodes.add(hexCode)
        if (hexCode.id >= hexCodeIdCounter) {
            hexCodeIdCounter = hexCode.id + 1
        }
    }

    fun seedPalette(palette: Palette) {
        palettes.removeIf { it.id == palette.id }
        palettes.add(
            palette.copy(
                hexCodes = palette.hexCodes?.toMutableList(),
                products = palette.products?.toMutableList()
            )
        )
        if (palette.id >= paletteIdCounter) {
            paletteIdCounter = palette.id + 1
        }
    }

    fun seedProduct(product: Product) {
        products.removeIf { it.id == product.id }
        products.add(product)
        if (product.id >= productIdCounter) {
            productIdCounter = product.id + 1
        }
    }


    fun usersSnapshot(): List<User> = users.map { it.copy(
        friends = it.friends.toMutableList(),
        pastProducts = it.pastProducts.toMutableList(),
        currentProducts = it.currentProducts.toMutableList(),
        wishList = it.wishList.toMutableList(),
        personas = it.personas.toMutableList()
    ) }

    override suspend fun signUp_new_user(user_email: String, user_password: String): Int {
        if (users.any { it.email.equals(user_email, ignoreCase = true) }) {
            error("user_already_exists")
        }

        val id = userIdCounter++
        val user = User(
            id = id,
            username = user_email,
            password = user_password,
            email = user_email,
            firstname = "",
            lastname = "",
            defaultPersona = null
        )
        users.add(user)
        return id
    }

    override suspend fun signIn_user(user_username: String, user_password: String): Int {
        val user = users.find { it.username.equals(user_username, ignoreCase = true) }
            ?: error("user_not_found")

        if (user.password != user_password) {
            error("invalid_password")
        }
        return user.id
    }

    override suspend fun fetch_hex_code_from_id(id: Int): HexCode {
        return hexCodes.find { it.id == id.toLong() } ?: error("hex_code_not_found")
    }

    override suspend fun fetch_hex_code_from_hex_code_string(hex_code: String): HexCode {
        return hexCodes.find { it.hexCode.equals(hex_code, ignoreCase = true) }
            ?: error("hex_code_not_found")
    }

    override suspend fun fetch_palette_from_id(id: Int?): Palette {
        return palettes.find { it.id == id } ?: error("palette_not_found")
    }

    override suspend fun fetch_palette_from_name(name: String): Palette {
        return palettes.find { it.name.equals(name, ignoreCase = true) }
            ?: error("palette_not_found")
    }

    override suspend fun fetch_product_from_id(id: Int): Product {
        return products.find { it.id == id } ?: error("product_not_found")
    }

    override suspend fun fetch_user_from_id(id: Int): User {
        return users.find { it.id == id } ?: error("user_not_found")
    }

    override suspend fun fetch_user_from_email(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun add_user(user: User): Int {
        val existing = users.find { it.email.equals(user.email, ignoreCase = true) }
        if (existing != null) {
            existing.username = user.username
            existing.password = user.password
            existing.firstname = user.firstname
            existing.lastname = user.lastname
            existing.defaultPersona = user.defaultPersona
            existing.friends = user.friends.toMutableList()
            existing.pastProducts = user.pastProducts.toMutableList()
            existing.currentProducts = user.currentProducts.toMutableList()
            existing.wishList = user.wishList.toMutableList()
            existing.personas = user.personas.toMutableList()
            return existing.id
        }

        val id = if (user.id != 0) user.id else userIdCounter++
        val copy = user.copy(
            id = id,
            friends = user.friends.toMutableList(),
            pastProducts = user.pastProducts.toMutableList(),
            currentProducts = user.currentProducts.toMutableList(),
            wishList = user.wishList.toMutableList(),
            personas = user.personas.toMutableList()
        )
        users.add(copy)
        return id
    }

    override suspend fun remove_user(id: Int) {
        users.removeIf { it.id == id }
    }

    override suspend fun add_item_id_to_user_list(user_id: Int, item_id: Int, list: String) {
        val user = fetch_user_from_id(user_id)
        val targetList = when (list) {
            "friends" -> user.friends
            "pastProducts" -> user.pastProducts
            "currentProducts" -> user.currentProducts
            "wishList" -> user.wishList
            "personas" -> user.personas
            else -> error("invalid_list: $list")
        }
        if (!targetList.contains(item_id)) {
            targetList.add(item_id)
        }
    }

    override suspend fun remove_item_id_from_user_list(user_id: Int, item_id: Int, list: String) {
        val user = fetch_user_from_id(user_id)
        val targetList = when (list) {
            "friends" -> user.friends
            "pastProducts" -> user.pastProducts
            "currentProducts" -> user.currentProducts
            "wishList" -> user.wishList
            "personas" -> user.personas
            else -> error("invalid_list: $list")
        }
        targetList.remove(item_id)
    }

    override suspend fun update_user_column_string(user_id: Int, column: String, new_value: String) {
        val user = fetch_user_from_id(user_id)
        when (column) {
            "username" -> user.username = new_value
            "firstname" -> user.firstname = new_value
            "lastname" -> user.lastname = new_value
            "email" -> user.email = new_value
            "password" -> user.password = new_value
            else -> error("unsupported_user_column: $column")
        }
    }

    override suspend fun add_persona_to_user(user_id: Int, new_persona_id: Int) {
        val user = fetch_user_from_id(user_id)
        user.personas.add(new_persona_id)
    }

    override suspend fun share_persona(from: Int, to: Int, persona: Int) {
        val existing = sharedPersonas.find {
            it.persona == persona && it.owner == to
        }

        if (existing != null) {
            return
        }

        val id = sharedPersonaCounter++
        val newShare = SharedPersonas(
            id = id,
            persona = persona,
            owner = to
        )

        sharedPersonas.add(newShare)
    }

    override suspend fun update_user_default_persona(user_id: Int, new_persona_id: Int) {
        val user = fetch_user_from_id(user_id)
        user.defaultPersona = new_persona_id
    }

    override suspend fun fetch_persona_from_id(id: Int): Persona {
        return personas.find { it.id == id } ?: error("persona_not_found")
    }

    override suspend fun fetch_persona_from_ids(ids: List<Int>): List<Persona> {
        return personas.filter { it.id in ids }
    }

    override suspend fun fetch_shared_persona_from_ids(ids: List<Int>): List<FESharedPersonas> {
        return FESharedPersonas.filter { it.SPid in ids }
    }

    override suspend fun add_persona(persona: Persona): Int {
        val existing = personas.find { it.id == persona.id }
        if (existing != null) {
            personas[personas.indexOf(existing)] = persona
            return persona.id
        }

        val id = if (persona.id != 0) persona.id else personaIdCounter++
        val copy = persona.copy(
            id = id,
            recommendedProducts = persona.recommendedProducts?.toMutableList(),
            //recommendedColors = persona.recommendedColors?.toMutableList()
        )
        personas.add(copy)
        return id
    }

    override suspend fun remove_persona(id: Int, remover: Int): String {
        personas.removeIf { it.id == id }
        return "Success"
    }

    override suspend fun add_item_id_to_persona_list(persona_id: Int, item_id: Int, list: String, curUser: Int) {
        val persona = fetch_persona_from_id(persona_id)
        when (list) {
            "recommendedProducts" -> {
                val items = persona.recommendedProducts ?: mutableListOf<Int>().also {
                    persona.recommendedProducts = it
                }
                if (!items.contains(item_id)) items.add(item_id)
            }
            /*
            "recommendedColors" -> {
                val items = persona.recommendedColors ?: mutableListOf<Int>().also {
                    persona.recommendedColors = it
                }
                if (!items.contains(item_id)) items.add(item_id)
            }
            */
            else -> error("invalid_persona_list: $list")
        }
    }

    override suspend fun update_persona_name(persona_id: Int, new_name: String, curUser: Int) {
        val persona = fetch_persona_from_id(persona_id)
        personas[personas.indexOf(persona)] = persona.copy(name = new_name)
    }

    override suspend fun acceptPersona(personaId: Int, SPId: Int, UserId: Int) {
        val user = fetch_user_from_id(UserId)

        user.sharedPersonas = (user.sharedPersonas ?: mutableListOf()).toMutableList().also { it.remove(SPId) }
        user.personas.add(personaId)
        sharedPersonas.removeIf { it.id == SPId }
    }

    override suspend fun rejectPersona(personaId: Int, SPId: Int, UserId: Int) {
        val user = fetch_user_from_id(UserId)

        user.sharedPersonas = (user.sharedPersonas ?: mutableListOf()).toMutableList().also { it.remove(SPId) }
        sharedPersonas.removeIf { it.id == SPId }
    }

    override suspend fun send_notification_to_affected_users(persona_changed: Int, changed_by: User, notification: String) {}

    override suspend fun update_persona_colour_analysis_result(persona_id: Int, new_CAR_id: Int?, curUser: Int) {
        val persona = fetch_persona_from_id(persona_id)
        personas[personas.indexOf(persona)] = persona.copy(colourAnalysisResult = new_CAR_id)
    }

    override suspend fun update_persona_profilepic(persona_id: Int, new_profilepic: Long?) {
        val persona = fetch_persona_from_id(persona_id)
        personas[personas.indexOf(persona)] = persona.copy(profilepic = new_profilepic)
    }

    override suspend fun fetch_extract_colours_from_id(id: Int?): ExtractedColours {
        return extractedColours.find { it.id == id } ?: error("extracted_colours_not_found")
    }

    override suspend fun add_extract_colours(extracted_colours: ExtractedColours): Int? {
        val existing = this.extractedColours.find { it.id == extracted_colours.id }
        if (existing != null) {
            this.extractedColours[this.extractedColours.indexOf(existing)] = extracted_colours
            return existing.id
        }

        val id = if (extracted_colours.id != 0) extracted_colours.id else extractedColoursIdCounter++
        val copy = extracted_colours.copy(id = id)
        this.extractedColours.add(copy)
        return id
    }

    override suspend fun remove_extract_colours(id: Int) {
        extractedColours.removeIf { it.id == id }
    }

    override suspend fun update_extract_colours_column_string(user_id: Int, column: String, new_value: String) {
        val existing = fetch_extract_colours_from_id(user_id)
        val updated = when (column) {
            "foreheadHex" -> existing.copy(foreheadHex = new_value)
            "cheekHex" -> existing.copy(cheekHex = new_value)
            "chinHex" -> existing.copy(chinHex = new_value)
            "hairHex" -> existing.copy(hairHex = new_value)
            "eyeHex" -> existing.copy(eyeHex = new_value)
            else -> error("invalid_extract_colours_column: $column")
        }
        extractedColours[extractedColours.indexOf(existing)] = updated
    }

    override suspend fun fetch_CAR_from_id(id: Int?): ColourAnalysisResult {
        return colourAnalysisResults.find { it.id == id } ?: error("car_not_found")
    }

    override suspend fun add_CAR(CAR: ColourAnalysisResult): Int? {
        val existing = colourAnalysisResults.find { it.id == CAR.id }
        if (existing != null) {
            colourAnalysisResults[colourAnalysisResults.indexOf(existing)] = CAR
            return existing.id
        }

        val id = if (CAR.id != 0) CAR.id else colourAnalysisResultIdCounter++
        val copy = CAR.copy(id = id)
        colourAnalysisResults.add(copy)
        return id
    }

    override suspend fun remove_CAR(id: Int) {
        colourAnalysisResults.removeIf { it.id == id }
    }

    override suspend fun update_CAR_column_string(user_id: Int, column: String, new_value: String) {
        val existing = fetch_CAR_from_id(user_id)
        val updated = when (column) {
            "undertone" -> existing.copy(undertone = new_value)
            "value" -> existing.copy(value = new_value)
            "contrast" -> existing.copy(contrast = new_value)
            "chroma" -> existing.copy(chroma = new_value)
            else -> error("invalid_car_column: $column")
        }
        colourAnalysisResults[colourAnalysisResults.indexOf(existing)] = updated
    }

    override suspend fun update_CAR_column_int(user_id: Int, column: String, new_value: Int) {
        val existing = fetch_CAR_from_id(user_id)
        val updated = when (column) {
            "extractedColours" -> existing.copy(extractedColours = new_value)
            "palette" -> existing.copy(palette = new_value)
            else -> error("invalid_car_int_column: $column")
        }
        colourAnalysisResults[colourAnalysisResults.indexOf(existing)] = updated
    }


    override suspend fun update_persona_recommended_products(persona_id: Int, product_ids: MutableList<Int>) {
        val persona = fetch_persona_from_id(persona_id)
        personas[personas.indexOf(persona)] = persona.copy(recommendedProducts = product_ids)
    }

    override suspend fun update_user_notifications(user_id: Int, new_notifications: List<String>) {
        val userIndex = users.indexOfFirst { it.id == user_id }
        if (userIndex != -1) {
            users[userIndex] = users[userIndex].copy(notifications = new_notifications as MutableList<String>)
        }
    }

}

