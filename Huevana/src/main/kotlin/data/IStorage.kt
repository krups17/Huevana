package org.example.data

import domain.Persona
import domain.Product
import domain.User
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.FESharedPersonas
import org.example.domain.HexCode
import org.example.domain.Palette

interface IStorage {
    // canonical operations
    suspend fun signUp_new_user(user_email: String, user_password: String): Int
    suspend fun signIn_user(user_username: String, user_password: String): Int
//    suspend fun get_hex_code_id_from_string(hex_code: String): Int

    //
    // HEX CODES table functions
    //
    suspend fun fetch_hex_code_from_id(id: Int): HexCode
    suspend fun fetch_hex_code_from_hex_code_string(hex_code: String): HexCode // 6 character string

    //
    // PALETTES table functions
    //
    suspend fun fetch_palette_from_id(id: Int?): Palette
    suspend fun fetch_palette_from_name(name: String): Palette
    // shouldn't need to add or remove palettes as there are only 12

    //
    // PRODUCTS table functions
    //
    suspend fun fetch_product_from_id(id: Int): Product

    //
    // USERS table functions
    //
    suspend fun fetch_user_from_id(id: Int): User
    suspend fun fetch_user_from_email(email: String): User?
    suspend fun add_user(user: User): Int // returns the user's unique id
    suspend fun remove_user(id: Int)

    // can add to friends, past_products, current_products, wishlist or personas
    suspend fun add_item_id_to_user_list(user_id: Int, item_id: Int, list: String)
    // can remove from friends, past_products, current_products, wishlist or personas
    suspend fun remove_item_id_from_user_list(user_id: Int, item_id: Int, list: String)

    // can update username, firstname, lastname
    suspend fun update_user_column_string(user_id: Int, column: String, new_value: String)
    suspend fun update_user_default_persona(user_id: Int, new_persona_id: Int)
    suspend fun add_persona_to_user(user_id: Int, new_persona_id: Int)
    suspend fun share_persona(from: Int, to: Int, persona: Int)

    //
    // PERSONAS table functions
    //
    suspend fun fetch_persona_from_id(id: Int): Persona
    suspend fun fetch_persona_from_ids(ids: List<Int>): List<Persona>
    suspend fun fetch_shared_persona_from_ids(ids: List<Int>): List<FESharedPersonas>
    suspend fun add_persona(persona: Persona): Int // returns the personas unique id
    suspend fun remove_persona(id: Int, remover: Int): String // returns success or error message

    // can add to recommendedProducts, or recommendedColors
    suspend fun add_item_id_to_persona_list(persona_id: Int, item_id: Int, list: String, curUser: Int)
    suspend fun update_persona_name(persona_id: Int, new_name: String, curUser: Int)
    suspend fun update_persona_colour_analysis_result(persona_id: Int, new_CAR_id: Int?, curUser: Int)
    suspend fun update_persona_profilepic(persona_id: Int, new_profilepic: Long?)
    suspend fun send_notification_to_affected_users(persona_changed: Int, changed_by: User, notification: String)

    //
    // SHARED PERSONAS table functions
    //
    suspend fun acceptPersona(personaId: Int, SPId: Int, UserId: Int)
    suspend fun rejectPersona(personaId: Int, SPId: Int, UserId: Int)

    //
    // EXTRACTED COLOURS table functions
    //
    suspend fun fetch_extract_colours_from_id(id: Int?): ExtractedColours
    suspend fun add_extract_colours(extracted_colours: ExtractedColours): Int? // returns the personas unique id
    suspend fun remove_extract_colours(id: Int)

    // can update foreheadHex, cheekHex, chinHex, hairHex, eyeHex
    suspend fun update_extract_colours_column_string(user_id: Int, column: String, new_value: String)

    //
    // COLOUR ANALYSIS RESULT table functions
    //
    suspend fun fetch_CAR_from_id(id: Int?): ColourAnalysisResult
    suspend fun add_CAR(CAR: ColourAnalysisResult): Int? // returns the personas unique id
    suspend fun remove_CAR(id: Int)

    // can update undertone, value, contrast, chroma
    suspend fun update_CAR_column_string(user_id: Int, column: String, new_value: String)
    // can update extractedColours (id), palette (id)
    suspend fun update_CAR_column_int(user_id: Int, column: String, new_value: Int)

    suspend fun update_persona_recommended_products(persona_id: Int, product_ids: MutableList<Int>)
    suspend fun update_user_notifications(user_id: Int, new_notifications: List<String>)

}