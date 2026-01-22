package org.example.presentation

import androidx.compose.runtime.mutableStateOf
import domain.Model
import domain.Persona
import domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.domain.Palette
import org.example.domain.ColourAnalysisResult

class HomepageViewModel(val model: Model) {
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            val persona = model.fetch_persona_from_id(model.currentPersonaId)
        }
    }

    suspend fun fetch_persona_from_id(id: Int): Persona {
        return model.fetch_persona_from_id(id)
    }

    suspend fun fetch_current_persona_from_id(): Persona {
        return model.fetch_persona_from_id(model.currentPersonaId)
    }

    fun getSizeMutableList (list: MutableList<Int>?): Int {
        var result = 0
        for (i in 0 until (list?.size ?: 0)) {
            result += 1
        }
        return result
    }

    suspend fun fetch_product_from_id(id: Int): Product {
        return model.fetch_product_from_id(id)
    }
    suspend fun fetch_palette_from_id(id: Int?): Palette {
        return model.fetch_palette_from_id(id)
    }
    suspend fun fetch_CAR_from_id(id: Int?): ColourAnalysisResult {
        return model.fetch_CAR_from_id(id)
    }

    suspend fun addProductToWishlist(productId: Int) {
        // Follow the same pattern as addPersona in PersonaPageViewModel
        if (model.currentUserId == -1) return
        model.addToWishList(model.currentUserId, productId)
    }

    suspend fun removeProductFromWishlist(productId: Int) {
        if (model.currentUserId == -1) return
        model.removeFromWishList(model.currentUserId, productId)
    }

    suspend fun isProductInWishlist(productId: Int): Boolean {
        if (model.currentUserId == -1) return false

        // Ensure user is in cache
        val user = model.users.find { it?.id == model.currentUserId }
        if (user != null) {
            return user.wishList.contains(productId)
        }

        // If not in cache, try to fetch user
        return try {
            val fetchedUser = model.fetch_user_from_id(model.currentUserId)
            if (model.users.find { it?.id == fetchedUser.id } == null) {
                model.users.add(fetchedUser)
            }
            fetchedUser.wishList.contains(productId)
        } catch (e: Exception) {
            false
        }
    }
    fun dropMenuFilter(filter: String, currentRecommendedProducts: MutableList<Int>?) : MutableList<Int> {
        var filteredProducts: MutableList<Int> = mutableListOf()
        var currentProduct = Product(id = 0, "", "", "", 1, 1.toFloat(), "")
        var currentProductId: Int? = 0
        var currentProductName = ""
        for (i in 0 until (currentRecommendedProducts?.size ?: 0)) {
            currentProductId = currentRecommendedProducts?.get(i)
            runBlocking {
                launch {
                    currentProduct = model.fetch_product_from_id(currentProductId?:0)
                    currentProductName = currentProduct.name
                }
            }
            if (currentProductName.contains(filter)) {
                filteredProducts.add(currentProductId?:82)
            }
        }
        return filteredProducts
    }
}