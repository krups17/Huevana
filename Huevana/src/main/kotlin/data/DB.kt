package org.example.data

import domain.Persona
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import domain.Product
import domain.User
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.bridj.util.Utils.eq
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.FESharedPersonas
import org.example.domain.HexCode
import org.example.domain.Palette
import org.example.domain.SharedPersonas

class DB(database: String): IStorage {

    val supabase = createSupabaseClient(
        supabaseUrl = "https://vrknmhxkboedagxkvmwb.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZya25taHhrYm9lZGFneGt2bXdiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjIyOTA3MTksImV4cCI6MjA3Nzg2NjcxOX0.VrIZHl1KA0sOd3nYEdT3AetsvOllpeYzeqfl-0eK4fk",

        ) {
        install(Auth)
        install(Postgrest)
        //install other modules

    }
    val session = supabase.auth.currentSessionOrNull()

    suspend fun init() {
        supabase.auth.sessionStatus.collect {
            when(it) {
                is SessionStatus.Authenticated -> {
                    println("Received new authenticated session.")
                    when(it.source) { //Check the source of the session
                        SessionSource.External -> TODO()
                        is SessionSource.Refresh -> TODO()
                        is SessionSource.SignIn -> TODO()
                        is SessionSource.SignUp -> TODO()
                        SessionSource.Storage -> TODO()
                        SessionSource.Unknown -> TODO()
                        is SessionSource.UserChanged -> TODO()
                        is SessionSource.UserIdentitiesChanged -> TODO()
                        else -> {}
                    }
                }
                SessionStatus.Initializing -> println("Initializing")
                is SessionStatus.RefreshFailure -> {
                    println("Session expired and could not be refreshed")
                }
                is SessionStatus.NotAuthenticated -> {
                    if(it.isSignOut) {
                        println("User signed out")
                    } else {
                        println("User not signed in")
                    }
                }
            }
        }
    }

    override suspend fun signUp_new_user(user_email: String, user_password: String): Int {
        val result = supabase
            .auth
            .signUpWith(Email) {
                email = user_email
                password = user_password
            }
        val session = supabase.auth.refreshCurrentSession()
        return 0
    }

    override suspend fun signIn_user(user_username: String, user_password: String): Int {
        // First, look up the user by username to get their email
        val allUsers = supabase
            .from("Users")
            .select()
            .decodeList<JsonElement>()

        var userEmail: String? = null
        var userId: Int? = null

        for (user in allUsers) {
            val username = user.jsonObject["username"]?.jsonPrimitive?.content
            if (username == user_username) {
                userEmail = user.jsonObject["email"]!!.jsonPrimitive.content
                userId = user.jsonObject["id"]!!.jsonPrimitive.int
                break
            }
        }

        if (userEmail == null || userId == null) {
            println("ERROR: Can't find username in Users table: $user_username")
            throw IllegalStateException("Please enter a valid username.")
        }

        // Now authenticate with Supabase Auth using the email
        val result = supabase
            .auth
            .signInWith(Email) {
                email = userEmail
                password = user_password
            }
        val session = supabase.auth.refreshCurrentSession()

        return userId
    }

//    override suspend fun get_hex_code_id_from_string(hex_code: String): Int {
//        val result = supabase
//            .from("Hex Codes")
//            .select(columns = Columns.list("id")){
//                filter {
//                    eq("hex_code", hex_code)
//                }
//            }
//            .decodeSingle<Int>()
//        return result
//    }

    override suspend fun fetch_hex_code_from_id(id: Int): HexCode {
        val result = supabase
            .from("Hex Codes")
            .select(){
                filter{
                    eq("id", id)
                }
            }
            .decodeSingle<HexCode>()
        return result

    }

    //
    // HEX CODES table functions
    //
    override suspend fun fetch_hex_code_from_hex_code_string(hex_code: String): HexCode {
        val result = supabase
            .from("Hex Codes")
            .select(){
                filter{
                    eq("hex_code", hex_code)
                }
            }
            .decodeSingle<HexCode>()
        return result
    }

    //
    // PALETTES table functions
    //
    override suspend fun fetch_palette_from_id(id: Int?): Palette {
        val result = supabase
            .from("Palettes")
            .select()
            .decodeList<Palette>()
            .first { it.id == id }

        return result
    }

    override suspend fun fetch_palette_from_name(name: String): Palette {
        val result = supabase
            .from("Palettes")
            .select(){
                filter{
                    eq("name", name)
                }
            }
            .decodeSingle<Palette>()
        return result
    }

    //
    // PRODUCTS table functions
    //
    override suspend fun fetch_product_from_id(id: Int): Product {
        val result = supabase
            .from("Products")
            .select() {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Product>()
        return result
    }

    //
    // USERS table functions
    //
    override suspend fun fetch_user_from_id(id: Int): User {
        val result = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<User>()
        return result
    }


    override suspend fun fetch_user_from_email(email: String): User? {
        val allUsers = supabase
            .from("Users")
            .select() { }
            .decodeList<User>()

        allUsers.forEach { user ->
            if (user.email == email) {
                return user
            }
        }

        return null
    }

    override suspend fun add_user(user: User): Int {
        val payload = buildJsonObject {
            put("username", JsonPrimitive(user.username))
            put("password", JsonPrimitive(user.password))
            put("email", JsonPrimitive(user.email))
            put("firstname", JsonPrimitive(user.firstname))
            put("lastname", JsonPrimitive(user.lastname))
            put(
                "friends",
                buildJsonArray {
                    user.friends.forEach { add(JsonPrimitive(it)) }
                }
            )
            put(
                "pastProducts",
                buildJsonArray {
                    user.pastProducts.forEach { add(JsonPrimitive(it)) }
                }
            )
            put(
                "currentProducts",
                buildJsonArray {
                    user.currentProducts.forEach { add(JsonPrimitive(it)) }
                }
            )
            put(
                "wishList",
                buildJsonArray {
                    user.wishList.forEach { add(JsonPrimitive(it)) }
                }
            )
            put(
                "personas",
                buildJsonArray {
                    user.personas.forEach { add(JsonPrimitive(it)) }
                }
            )
            if (user.defaultPersona != null) {
                put("defaultPersona", JsonPrimitive(user.defaultPersona))
            } else {
                put("defaultPersona", JsonNull)
            }
        }

        val result = supabase
            .from("Users")
            .insert(payload) {
                select()
            }
            .decodeSingle<User>()
        return result.id
    }

    override suspend fun remove_user(id: Int) {
        val result = supabase
            .from("Users")
            .delete(
                {
                    filter {
                        eq("id", id)
                    }
                }
            )
    }

    override suspend fun add_item_id_to_user_list(user_id: Int, item_id: Int, list: String) {
        // Follow the EXACT same pattern as add_persona_to_user - fetch whole user first
        val user = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", user_id)
                }
            }
            .decodeSingle<User>()

        // Get the current list from the user object and add the new item
        val currentList = when (list) {
            "wishList" -> (user.wishList + item_id).distinct()
            "currentProducts" -> (user.currentProducts + item_id).distinct()
            "pastProducts" -> (user.pastProducts + item_id).distinct()
            "friends" -> (user.friends + item_id).distinct()
            "personas" -> (user.personas + item_id).distinct()
            else -> emptyList()
        }

        // Update the database with the new list - same pattern as personas
        supabase
            .from("Users")
            .update(
                {
                    set(list, currentList)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    override suspend fun remove_item_id_from_user_list(user_id: Int, item_id: Int, list: String) {
        // Follow the EXACT same pattern as add_item_id_to_user_list - fetch whole user first
        val user = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", user_id)
                }
            }
            .decodeSingle<User>()

        // Get the current list from the user object and remove the item
        val currentList = when (list) {
            "wishList" -> user.wishList.filter { it != item_id }
            "currentProducts" -> user.currentProducts.filter { it != item_id }
            "pastProducts" -> user.pastProducts.filter { it != item_id }
            "friends" -> user.friends.filter { it != item_id }
            "personas" -> user.personas.filter { it != item_id }
            else -> emptyList()
        }

        // Update the database with the new list - same pattern as add_item_id_to_user_list
        supabase
            .from("Users")
            .update(
                {
                    set(list, currentList)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    override suspend fun update_user_column_string(user_id: Int, column: String, new_value: String) {
        val result = supabase
            .from("Users")
            .update(
                {
                    set(column, new_value)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    override suspend fun update_user_default_persona(user_id: Int, new_persona_id: Int) {
        val result = supabase
            .from("Users")
            .update(
                {
                    set("defaultPersona", new_persona_id)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    override suspend fun add_persona_to_user(user_id: Int, new_persona_id: Int) {
        val user = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", user_id)
                }
            }
            .decodeSingle<User>()

        val updatedPersonaList = (user.personas + new_persona_id).distinct()

        supabase
            .from("Users")
            .update(
                {
                    set("personas", updatedPersonaList)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    override suspend fun share_persona(from: Int, to: Int, persona: Int) {
        // add to shared persona table
        val payload = buildJsonObject {
            put("persona", JsonPrimitive(persona))
            put("owner", JsonPrimitive(from))
        }

        val sharedPersona = supabase
            .from("Shared Personas")
            .insert(payload) {
                select()
            }
            .decodeSingle<SharedPersonas>()

        // add to receiving user shared persona column
        val receivingUser = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", to)
                }
            }
            .decodeSingle<User>()

        val sharingUser = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", from)
                }
            }
            .decodeSingle<User>()

        val updatedSharedPersonas = ((receivingUser.sharedPersonas ?: emptyList()) + sharedPersona.id).distinct()
        // add notification to the receiving user that current user shared a persona with that
        val notification = "${sharingUser.firstname} has shared a persona with you. Go to the Personas page to accept or reject."
        val updatedNotifications = (receivingUser.notifications + notification).distinct()

        supabase
            .from("Users")
            .update(
                {
                    set("sharedPersonas", updatedSharedPersonas)
                    set("notifications", updatedNotifications)
                }
            )
            {
                filter {
                    eq("id", to)
                }
            }
    }

    //
    // PERSONAS table functions
    //
    override suspend fun fetch_persona_from_id(id: Int): Persona {
        val result = supabase
            .from("Personas")
            .select() {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<Persona>()

        return result
    }

    override suspend fun fetch_persona_from_ids(ids: List<Int>): List<Persona> {
        val result = supabase
            .from("Personas")
            .select()
            .decodeList<Persona>()
        val selected = result
            .filter { ids.contains(it.id) }

        return selected
    }

    override suspend fun fetch_shared_persona_from_ids(ids: List<Int>): List<FESharedPersonas> {
        val result = supabase
            .from("Shared Personas")
            .select()
            .decodeList<SharedPersonas>()
        val selected = result
            .filter { ids.contains(it.id) }

        return selected.map { SP ->
            val persona = fetch_persona_from_id(SP.persona)
            val sender = fetch_user_from_id(SP.owner)

            FESharedPersonas(
                SPid = SP.id,
                persona = persona.name,
                personaId = persona.id,
                owner = sender.firstname,
                profilepic = persona.profilepic
            )
        }
    }

    override suspend fun add_persona(persona: Persona): Int {
        val payload = buildJsonObject {
            put("name", JsonPrimitive(persona.name))
            put(
                "recommendedProducts",
                buildJsonArray {
                    persona.recommendedProducts?.forEach { add(JsonPrimitive(it)) }
                }
            )
            if (persona.colourAnalysisResult != null) {
                put("colourAnalysisResult", JsonPrimitive(persona.colourAnalysisResult))
            } else {
                put("colourAnalysisResult", JsonNull)
            }
            if (persona.profilepic != null) {
                put("profilepic", JsonPrimitive(persona.profilepic))
            } else {
                put("profilepic", JsonNull)
            }
        }

        val result = supabase
            .from("Personas")
            .insert(payload) {
                select()
            }
            .decodeSingle<Persona>()
        return result.id
    }

    override suspend fun remove_persona(id: Int, remover: Int): String {
        // if ANY user has this persona as the default persona, DO NOT DELETE
        val errorUsers = supabase
            .from("Users")
            .select() {
                filter {
                    eq("defaultPersona", id)
                }
            }
            .decodeList<User>()

        if (errorUsers.isNotEmpty()) {
            return("Error: Cannot delete a default persona")
        }

        // now remove this persona from persona list for all users
        val remover = supabase
            .from("Users")
            .select() {
                filter {
                    eq("id", remover)
                }
            }
            .decodeSingle<User>()

        val persona = fetch_persona_from_id(id)
        val notification = "${remover.firstname} deleted the shared persona: ${persona.name}."

        val affectedUsers = supabase
            .from("Users")
            .select() {
                filter {
                    contains("personas", listOf(id))
                }
            }
            .decodeList<User>()

        affectedUsers.forEach { user ->
            val newPersonaList = user.personas.filter { it.toInt() != id }
            val notifications =
                if (user.id != remover.id) {
                    (user.notifications + notification).distinct()
                } else {
                    user.notifications
                }

            supabase
                .from("Users")
                .update(
                    {
                        set("personas", newPersonaList)
                        set("notifications", notifications)
                    }
                ) {
                    filter { eq("id", user.id) }
                }
        }

        // delete the persona
        supabase
            .from("Personas")
            .delete(
                {
                    filter {
                        eq("id", id)
                    }
                }
            )

        return "Successfully Deleted Persona"
    }


    override suspend fun send_notification_to_affected_users(persona_changed: Int, changed_by: User, notification: String) {
        val affectedUsers = supabase
            .from("Users")
            .select() {
                filter {
                    contains("personas", listOf(persona_changed))
                }
            }
            .decodeList<User>()

        affectedUsers.forEach { user ->
            val notifications =
                if (user.id != changed_by.id) {
                    (user.notifications + notification).distinct()
                } else {
                    user.notifications
                }

            supabase
                .from("Users")
                .update(
                    {
                        set("notifications", notifications)
                    }
                ) {
                    filter { eq("id", user.id) }
                }
        }
    }

    override suspend fun update_persona_name(persona_id: Int, new_name: String, curUser: Int) {
        val result = supabase
            .from("Personas")
            .update(
                {
                    set("name", new_name)
                }
            )
            {
                filter {
                    eq("id", persona_id)
                }
            }

        val changedByUser = fetch_user_from_id(curUser)
        val notification = "${changedByUser.firstname} changed the names of one of your shared Persona to $new_name. Go to the Persona page to view the change."
        send_notification_to_affected_users(persona_id, changedByUser, notification)
    }

    override suspend fun acceptPersona(personaId: Int, SPId: Int, UserId: Int) {
        // remove sp id from current user's SP column
        val currentUser = fetch_user_from_id(UserId)
        val updatedSP = (currentUser.sharedPersonas ?: emptyList())
            .filter { it != SPId }

        // add persona ID to current user's persona list
        val updatedPersonaList = (currentUser.personas + personaId).distinct()
        supabase
            .from("Users")
            .update(
                {
                    set("personas", updatedPersonaList)
                    set("sharedPersonas", updatedSP)
                }
            )
            {
                filter {
                    eq("id", UserId)
                }
            }

        // add notification to the user who shared the persona
        val sharedPersona = supabase
            .from("Shared Personas")
            .select() {
                filter {
                    eq("id", SPId)
                }
            }
            .decodeSingle<SharedPersonas>()

        val ownerId = sharedPersona.owner
        val owner = fetch_user_from_id(ownerId)
        val persona = fetch_persona_from_id(personaId)

        val notification = "${currentUser.firstname} has accept the persona you shared: ${persona.name}."
        val notifications = (owner.notifications + notification).distinct()

        supabase
            .from("Users")
            .update(
                {
                    set("notifications", notifications)
                }
            ) {
                filter { eq("id", ownerId) }
            }

        // delete SP entry since accepted (otherwise later deleting this persona won't work)
        supabase
            .from("Shared Personas")
            .delete(
                {
                    filter {
                        eq("id", SPId)
                    }
                }
            )
    }

    override suspend fun rejectPersona(personaId: Int, SPId: Int, UserId: Int) {
        // remove sp id from current user's SP column
        val user = fetch_user_from_id(UserId)
        val updatedSP = (user.sharedPersonas ?: emptyList())
            .filter { it != SPId }
        supabase
            .from("Users")
            .update(
                {
                    set("sharedPersonas", updatedSP)
                }
            )
            {
                filter {
                    eq("id", UserId)
                }
            }

        // we do not notify the sender - an IG logic that if you reject, the sharer doesn't need to know

        // delete SP entry since accepted (otherwise later deleting this persona won't work)
        supabase
            .from("Shared Personas")
            .delete(
                {
                    filter {
                        eq("id", SPId)
                    }
                }
            )
    }

    override suspend fun update_persona_colour_analysis_result(persona_id: Int, new_CAR_id: Int?, curUser: Int) {
        val result = supabase
            .from("Personas")
            .update(
                {
                    set("colourAnalysisResult", new_CAR_id)
                }
            )
            {
                filter {
                    eq("id", persona_id)
                }
            }

        val persona = fetch_persona_from_id(persona_id)
        val changedByUser = fetch_user_from_id(curUser)
        val notification = "${changedByUser.firstname} changed the color analysis of Persona ${persona.name}. Go to the Persona page to view the change."
        send_notification_to_affected_users(persona_id, changedByUser, notification)
    }

    override suspend fun update_persona_profilepic(persona_id: Int, new_profilepic: Long?) {
        val result = supabase
            .from("Personas")
            .update(
                {
                    if (new_profilepic != null) {
                        set("profilepic", new_profilepic)
                    } else {
                        set("profilepic", JsonNull)
                    }
                }
            )
            {
                filter {
                    eq("id", persona_id)
                }
            }
    }



    override suspend fun add_item_id_to_persona_list(persona_id: Int, item_id: Int, list: String, curUser: Int) {
        val list_result = supabase
            .from("Personas")
            .select(columns = Columns.list(list)) {
                filter {
                    eq("id", persona_id)
                }
            }
            .decodeList<Int>()
        var new_list = mutableListOf<Int>()
        val size = list_result.size
        for (i in 0 until size) {
            new_list.add(list_result[i])
        }
        new_list.add(item_id)
        val result = supabase
            .from("Personas")
            .update(
                {
                    set(list, new_list)
                }
            )
            {
                filter {
                    eq("id", persona_id)
                }
            }


        val persona = fetch_persona_from_id(persona_id)
        val changedByUser = fetch_user_from_id(curUser)
        val notification = "${changedByUser.firstname} added an item to $list for Persona ${persona.name}. Go to the Persona page to view the change."
        send_notification_to_affected_users(persona_id, changedByUser, notification)
    }

    //
    // EXTRACTED COLOURS table functions
    //
    override suspend fun fetch_extract_colours_from_id(id: Int?): ExtractedColours {
        val result = supabase
            .from("Extracted Colours")
            .select() {
                filter {
                    eq("id", id.toString())
                }
            }
            .decodeSingle<ExtractedColours>()

        println("DEBUG: Fetched result with ID: ${result.id}")
        return result
    }

    override suspend fun add_extract_colours(extracted_colours: ExtractedColours): Int {
        val payload = buildJsonObject {
            put("foreheadHex", JsonPrimitive(extracted_colours.foreheadHex))
            put("cheekHex", JsonPrimitive(extracted_colours.cheekHex))
            put("chinHex", JsonPrimitive(extracted_colours.chinHex))
            put("hairHex", JsonPrimitive(extracted_colours.hairHex))
            if (extracted_colours.eyeHex != null) {
                put("eyeHex", JsonPrimitive(extracted_colours.eyeHex))
            } else {
                put("eyeHex", JsonNull)
            }
        }

        val result = supabase
            .from("Extracted Colours")
            .insert(payload) {
                select()
            }
            .decodeSingle<ExtractedColours>()
        return result.id!!
    }

    override suspend fun add_CAR(CAR: ColourAnalysisResult): Int {
        val payload = buildJsonObject {
            put("undertone", JsonPrimitive(CAR.undertone))
            put("value", JsonPrimitive(CAR.value))
            put("contrast", JsonPrimitive(CAR.contrast))
            put("chroma", JsonPrimitive(CAR.chroma))
            put("extractedColours", JsonPrimitive(CAR.extractedColours))
            put("palette", JsonPrimitive(CAR.palette))
        }

        val result = supabase
            .from("Colour Analysis Result")
            .insert(payload) {
                select()
            }
            .decodeSingle<ColourAnalysisResult>()
        return result.id!!
    }

    override suspend fun remove_extract_colours(id: Int) {
        val result = supabase
            .from("Extracted Colours")
            .delete(
                {
                    filter {
                        eq("id", id)
                    }
                }
            )
    }

    override suspend fun update_extract_colours_column_string(user_id: Int, column: String, new_value: String) {
        val result = supabase
            .from("Extracted Colours")
            .update(
                {
                    set(column, new_value)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    //
    // COLOUR ANALYSIS RESULT table functions
    //
    override suspend fun fetch_CAR_from_id(id: Int?): ColourAnalysisResult {
        val result = supabase
            .from("Colour Analysis Result")
            .select() {
                filter {
                    eq("id", id.toString())
                }
            }
            .decodeSingle<ColourAnalysisResult>()

        return result
    }


    override suspend fun remove_CAR(id: Int) {
        val result = supabase
            .from("Colour Analysis Result")
            .delete(
                {
                    filter {
                        eq("id", id)
                    }
                }
            )
    }

    override suspend fun update_CAR_column_string(user_id: Int, column: String, new_value: String) {
        val result = supabase
            .from("Colour Analysis Result")
            .update(
                {
                    set(column, new_value)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    override suspend fun update_CAR_column_int(user_id: Int, column: String, new_value: Int) {
        val result = supabase
            .from("Colour Analysis Result")
            .update(
                {
                    set(column, new_value)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }

    //
    // Recomended Products Population
    //



    override suspend fun update_persona_recommended_products(persona_id: Int, product_ids: MutableList<Int>) {
        supabase
            .from("Personas")
            .update({
                set("recommendedProducts", product_ids)
            }) {
                filter {
                    eq("id", persona_id)
                }
            }
    }

    override suspend fun update_user_notifications(user_id: Int, new_notifications: List<String>) {
        supabase
            .from("Users")
            .update(
                {
                    set("notifications", new_notifications)
                }
            )
            {
                filter {
                    eq("id", user_id)
                }
            }
    }




}