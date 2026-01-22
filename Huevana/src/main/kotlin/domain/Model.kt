package domain

import io.github.jan.supabase.postgrest.from
import org.example.data.IStorage
import org.example.domain.ColourAnalysisResult
import org.example.domain.ExtractedColours
import org.example.domain.FESharedPersonas
import org.example.domain.HexCode
import org.example.domain.Palette


class Model(val storage: IStorage) {  // will add storage here later
    var users = mutableListOf<User?>()
    var products = mutableListOf<Product?>()
    var currentUserId = -1
    var currentPersonaId = -1

    // --------------------------
    // FUNCTIONS RELATED TO USER
    // --------------------------

    // consider checking usernames not the same
    fun addUser(username: String, password: String, email: String, firstname: String, lastname: String) {
        val persona = Persona(
            id = 1,
            name = "Me",
            mutableListOf(),
            colourAnalysisResult = 1,
        )

        val user = User(
            id = users.size + 1, username = username, password = password, email = email,
            firstname = firstname, lastname = lastname,
            defaultPersona = 1
        )
        users.add(user)
        // add to database
    }

    fun delUser(userId: Int) {
        val user = users.find { it?.id == userId } ?: return
        users.remove(user)
    }

    fun updatePassword(userId: Int, oldPassword: String, newPassword: String) {
        val user = users.find { it?.id == userId } ?: return
        if (user.password == oldPassword) {
            user.password = newPassword
        }
    }

    fun updateUsername(userId: Int, newUsername: String) {
        val user = users.find { it?.id == userId } ?: return
        user.username = newUsername
    }

    fun updateEmail(userId: Int, newEmail: String) {
        val user = users.find { it?.id == userId } ?: return
        user.email = newEmail
    }

    fun updateFirstName(userId: Int, newFN: String) {
        val user = users.find { it?.id == userId } ?: return
        user.firstname = newFN
    }

    fun updateLastName(userId: Int, newLN: String) {
        val user = users.find { it?.id == userId } ?: return
        user.lastname = newLN
    }

    //    Vinita can implement these + add test for these
    fun addFriend(userId: Int, friendId: Int) {
        val user = users.find { it?.id == userId } ?: return
        val friend = users.find { it?.id == friendId } ?: return
        if (!user.friends.contains(friend.id)) {
            user.friends.add(friend.id)
        }
    }

    fun removeFriend(userId: Int, friendId: Int) {
        val user = users.find { it?.id == userId } ?: return
        val friend = users.find { it?.id == friendId }
        user.friends.remove(friend?.id)
    }

    suspend fun addToPastProducts(userId: Int, productId: Int) { // should also remove from current products if that product is in there
        val user = users.find { it?.id == userId } ?: return
        val product = products.find { it?.id == productId } ?: return

        // Remove from current products in database if present
        if (user.currentProducts.contains(product.id)) {
            storage.remove_item_id_from_user_list(userId, product.id, "currentProducts")
            user.currentProducts.remove(product.id)
        }

        // Add to past products in database if not already there
        if (!user.pastProducts.contains(product.id)) {
            storage.add_item_id_to_user_list(userId, product.id, "pastProducts")
            user.pastProducts.add(product.id)
        }
    }

    suspend fun removeFromPastProducts(userId: Int, productId: Int) {
        // Remove from past products in database
        storage.remove_item_id_from_user_list(userId, productId, "pastProducts")
        // Update local cache
        val user = users.find { it?.id == userId }
        if (user != null) {
            user.pastProducts.remove(productId)
        }
    }

    suspend fun addToCurrentProducts(userId: Int, productId: Int) {
        val user = users.find { it?.id == userId } ?: return
        val product = products.find { it?.id == productId } ?: return

        // Remove from wishlist in database if present
        if (user.wishList.contains(product.id)) {
            storage.remove_item_id_from_user_list(userId, product.id, "wishList")
            user.wishList.remove(product.id)
        }

        // Add to current products in database if not already there
        if (!user.currentProducts.contains(product.id)) {
            storage.add_item_id_to_user_list(userId, product.id, "currentProducts")
            user.currentProducts.add(product.id)
        }
    }

    suspend fun removeFromCurrentProducts(userId: Int, productId: Int, addToPast: Boolean = false) { // can take in a bool as a field that indicates whether this should be added to PastProducts
        // Remove from current products in database
        storage.remove_item_id_from_user_list(userId, productId, "currentProducts")
        // Update local cache
        val user = users.find { it?.id == userId }
        if (user != null) {
            user.currentProducts.remove(productId)
        }

        // Optionally add to past products in database
        if (addToPast && user != null) {
            val product = products.find { it?.id == productId }
            if (product != null && !user.pastProducts.contains(product.id)) {
                storage.add_item_id_to_user_list(userId, product.id, "pastProducts")
                user.pastProducts.add(product.id)
            }
        }
    }

    suspend fun addToWishList(userId: Int, productId: Int) {
        // Add to wishlist in database
        storage.add_item_id_to_user_list(userId, productId, "wishList")
        // Update local cache
        val user = users.find { it?.id == userId }
        if (user != null && !user.wishList.contains(productId)) {
            user.wishList.add(productId)
        }
    }

    suspend fun removeFromWishList(userId: Int, productId: Int, addToCurrent: Boolean = false) { // bool as a field to decide if this should be added to current products
        // Remove from wishlist in database
        storage.remove_item_id_from_user_list(userId, productId, "wishList")
        // Update local cache
        val user = users.find { it?.id == userId }
        if (user != null) {
            user.wishList.remove(productId)
        }

        // Optionally add to current products
        if (addToCurrent) {
            val product = products.find { it?.id == productId }
            if (product != null && user != null && !user.currentProducts.contains(product.id)) {
                user.currentProducts.add(product.id)
            }
        }
    }
    // vinita end

    // ----------------------------
    // FUNCTIONS RELATED TO PERSONA
    // ----------------------------

    fun addPersona(userId: Int, givenName: String) {
        val currentUser = users.find { it?.id == userId } ?: return
        val persona = Persona(
            id = currentUser.personas.size + 1,
            name = givenName,
            recommendedProducts = mutableListOf(),
            colourAnalysisResult = 1,
        )
        currentUser.personas.add(persona.id)
    }

    // AK added
    fun delPersona(userId: Int, personaId: Int) {
        val user = users.find { it?.id == userId } ?: return
        val persona = user.personas.find { it == personaId } ?: return
        user.personas.remove(persona)
    }


    // Krupa added
    suspend fun editPersonaName(userId: Int, personaId: Int, newName: String) {
        val user = users.find { it?.id == userId } ?: return
        //val persona = user.personas.find { it?.id == personaId } ?: return
        storage.update_persona_name(personaId, newName, currentUserId)
    }

    /*
    fun analyzePersonaColors(
        userId: Int,
        personaId: Int,
        foreheadHex: String,
        cheekHex: String,
        chinHex: String,
        hairHex: String,
        eyeHex: String? = null
    ) {
        val user = users.find { it?.id == userId } ?: return
        val persona = user.personas.find { it?.id == personaId } ?: return

        // Store the personal coloring
        val coloring = PersonalColouring(
            foreheadHex = persona.foreheadHex,
            cheekHex = persona.cheekHex,
            chinHex = persona.chinHex,
            hairHex = persona.hairHex,
            eyeHex = persona.eyeHex
        )
        persona.personalColoring = coloring

        // Run the analysis
        val analysis = ColourAnalyzer.analyzeColoring(coloring)
        persona.seasonalAnalysis = analysis

        // Populate recommended colors
        persona.recommendedColors.clear()
        analysis.paletteColors.forEach { color ->
            persona.recommendedColors.add("#${color.toString(16).padStart(6, '0')}")
        }
    }
    */
    // Krupa end


    // ----------------------------
    // FUNCTIONS RELATED TO PRODUCT
    // ----------------------------

    fun addProduct(name: String, brand: String, imageUrl: String = "", palette: Int = 1, rating: Float = 0.0f, shade: String = "") {
        val product = Product(
            id = products.size + 1,
            name = name,
            brand = brand,
            imageUrl = imageUrl,
            palette = palette,
            rating = rating,
            shade = shade
        )
        products.add(product)
    }

    fun delProduct(productId: Int) {
        val product = products.find { it?.id == productId } ?: return
        products.remove(product)
    }

    fun updateProductName(productId: Int, newName: String) {
        val product = products.find { it?.id == productId } ?: return
        product.name = newName
    }

    fun updateProductBrand(productId: Int, newBrand: String) {
        val product = products.find { it?.id == productId } ?: return
        product.brand = newBrand
    }

    /*
        fun updateProductTutVid(productId: Int, newTutVid: Int) {
            val product = products.find { it?.id == productId } ?: return
            product.tutorialVideo = newTutVid
        }

        fun updateProductStoreRatings(productId: Int, SR: MutableList<Int>) {
            val product = products.find { it?.id == productId } ?: return
            product.storeRatings = SR
        }

        fun updateProductHex(productId: Int, newHex: MutableList<Int>) {
            val product = products.find { it?.id == productId } ?: return
            product.goodHexCodes = newHex
        }
    */
    ///
    /// DATABASE FUNCTIONS
    ///
    suspend fun signIn(email: String,password: String): Int {
        return storage.signIn_user(email, password)
    }
    suspend fun signUp(email: String,password: String): Int {
        return storage.signUp_new_user(email, password)
    }
    suspend fun signUp_new_user(user_email: String, user_password: String): Int {
        return storage.signUp_new_user(user_email, user_password)
    }
    suspend fun signIn_user(user_username: String, user_password: String): Int {
        return storage.signIn_user(user_username, user_password)
    }
    suspend fun fetch_hex_code_from_id(id: Int): HexCode {
        return storage.fetch_hex_code_from_id(id)
    }
    suspend fun fetch_hex_code_from_hex_code_string(hex_code: String): HexCode {
        return storage.fetch_hex_code_from_hex_code_string(hex_code)
    }
    suspend fun fetch_palette_from_id(id: Int?): Palette {
        return storage.fetch_palette_from_id(id)
    }
    suspend fun fetch_palette_from_name(name: String): Palette {
        return storage.fetch_palette_from_name(name)
    }
    suspend fun fetch_product_from_id(id: Int): Product {
        return storage.fetch_product_from_id(id)
    }
    suspend fun fetch_user_from_id(id: Int): User {
        return storage.fetch_user_from_id(id)
    }
    suspend fun fetch_user_from_email(email: String): User? {
        return storage.fetch_user_from_email(email)
    }
    suspend fun add_user(user: User): Int {
        return storage.add_user(user)
    }
    suspend fun remove_user(id: Int) {
        return storage.remove_user(id)
    }
    suspend fun add_item_id_to_user_list(user_id: Int, item_id: Int, list: String) {
        return storage.add_item_id_to_user_list(user_id, item_id, list)
    }
    suspend fun update_user_column_string(user_id: Int, column: String, new_value: String) {
        return storage.update_user_column_string(user_id, column, new_value)
    }
    suspend fun update_user_default_persona(user_id: Int, new_persona_id: Int) {
        return storage.update_user_default_persona(user_id, new_persona_id)
    }
    suspend fun add_persona_to_user(user_id: Int, new_persona_id: Int) {
        return storage.add_persona_to_user(user_id, new_persona_id)
    }
    suspend fun share_persona(from: Int, to: Int, persona: Int) {
        return storage.share_persona(from, to, persona)
    }
    suspend fun fetch_persona_from_id(id: Int): Persona {
        return storage.fetch_persona_from_id(id)
    }
    suspend fun fetch_persona_from_ids(ids: List<Int>): List<Persona> {
        return storage.fetch_persona_from_ids(ids)
    }
    suspend fun fetch_shared_persona_from_ids(ids: List<Int>): List<FESharedPersonas> {
        return storage.fetch_shared_persona_from_ids(ids)
    }
    suspend fun add_persona(persona: Persona): Int {
        return storage.add_persona(persona)
    }
    suspend fun remove_persona(id: Int, remover: Int): String {
        return storage.remove_persona(id, remover)
    }
    suspend fun add_item_id_to_persona_list(persona_id: Int, item_id: Int, list: String, curUser: Int) {
        return storage.add_item_id_to_persona_list(persona_id, item_id, list, curUser)
    }
    suspend fun update_persona_name(persona_id: Int, new_name: String, curUser: Int) {
        return storage.update_persona_name(persona_id, new_name, curUser)
    }
    suspend fun send_notification_to_affected_users(persona_changed: Int, changed_by: User, notification: String) {
        return storage.send_notification_to_affected_users(persona_changed, changed_by, notification)
    }
    suspend fun accept_persona(personaId: Int, SPId: Int, UserId: Int) {
        return storage.acceptPersona(personaId, SPId, UserId)
    }
    suspend fun reject_persona(personaId: Int, SPId: Int, UserId: Int) {
        return storage.rejectPersona(personaId, SPId, UserId)
    }
    suspend fun update_persona_colour_analysis_result(persona_id: Int, new_CAR_id: Int?, curUser: Int) {
        return storage.update_persona_colour_analysis_result(persona_id, new_CAR_id, curUser)
    }

    suspend fun update_persona_profilepic(persona_id: Int, new_profilepic: Long?) {
        return storage.update_persona_profilepic(persona_id, new_profilepic)
    }
    suspend fun fetch_extract_colours_from_id(id: Int?): ExtractedColours {
        return storage.fetch_extract_colours_from_id(id)
    }
    suspend fun add_extract_colours(extracted_colours: ExtractedColours): Int? {
        return storage.add_extract_colours(extracted_colours)
    }
    suspend fun remove_extract_colours(id: Int) {
        return storage.remove_extract_colours(id)
    }
    suspend fun update_extract_colours_column_string(user_id: Int, column: String, new_value: String) {
        return storage.update_extract_colours_column_string(user_id, column, new_value)
    }
    suspend fun fetch_CAR_from_id(id: Int?): ColourAnalysisResult {
        return storage.fetch_CAR_from_id(id)
    }
    suspend fun add_CAR(CAR: ColourAnalysisResult): Int? {
        return storage.add_CAR(CAR)
    }
    suspend fun remove_CAR(id: Int) {
        return storage.remove_CAR(id)
    }
    suspend fun update_CAR_column_string(user_id: Int, column: String, new_value: String) {
        return storage.update_CAR_column_string(user_id, column, new_value)
    }
    suspend fun update_CAR_column_int(user_id: Int, column: String, new_value: Int) {
        return storage.update_CAR_column_int(user_id, column, new_value)
    }



    suspend fun populate_persona_recommendations(personaId: Int) {
        try {
            val persona = storage.fetch_persona_from_id(personaId)
            val analysisResultId = persona.colourAnalysisResult ?: return
            val analysisResult = storage.fetch_CAR_from_id(analysisResultId)
            val paletteId = analysisResult.palette

            val palette = storage.fetch_palette_from_id(paletteId)
            val recommendedProducts = palette.products?.toMutableList() ?: mutableListOf()

            storage.update_persona_recommended_products(personaId, recommendedProducts)

            println("Populated persona $personaId with ${recommendedProducts.size} products from palette ${palette.name}")
        } catch (e: Exception) {
            println("Error populating recommendations: ${e.message}")
            e.printStackTrace()
        }
    }
suspend fun update_user_notifications(user_id: Int, new_notifications: List<String>) {
        return storage.update_user_notifications(user_id, new_notifications)
    }



}