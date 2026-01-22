package org.example.presentation

import androidx.compose.runtime.mutableStateOf
import domain.Model
import domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistPageViewModel(val model: Model) {
    val wishlistProducts = mutableStateOf<List<Product>>(emptyList())
    val isLoading = mutableStateOf(true)

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        loadWishlistProducts()
    }

    fun loadWishlistProducts() {
        isLoading.value = true
        scope.launch {
            try {
                println("Loading wishlist - currentUserId: ${model.currentUserId}")

                if (model.currentUserId == -1) {
                    println("ERROR: User not logged in (currentUserId == -1)")
                    wishlistProducts.value = emptyList()
                    isLoading.value = false
                    return@launch
                }

                val user = model.fetch_user_from_id(model.currentUserId)
                println("Fetched user ID: ${user.id}, wishList size: ${user.wishList.size}")
                println("Wishlist product IDs: ${user.wishList}")

                val productIds = user.wishList

                if (productIds.isEmpty()) {
                    println("Wishlist is empty (no product IDs)")
                    wishlistProducts.value = emptyList()
                    isLoading.value = false
                    return@launch
                }

                val products = mutableListOf<Product>()
                for (productId in productIds) {
                    try {
                        println("Fetching product ID: $productId")
                        val product = model.fetch_product_from_id(productId)
                        products.add(product)
                        println("Successfully loaded product: ${product.name}")
                    } catch (e: Exception) {
                        // Product might not exist, skip it
                        println("Failed to fetch product $productId: ${e.message}")
                        e.printStackTrace()
                    }
                }

                println("Total products loaded: ${products.size}")
                wishlistProducts.value = products
            } catch (e: Exception) {
                println("Failed to load wishlist: ${e.message}")
                e.printStackTrace()
                wishlistProducts.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }

    suspend fun removeProductFromWishlist(productId: Int) {
        if (model.currentUserId == -1) return
        model.removeFromWishList(model.currentUserId, productId)
        loadWishlistProducts() // Reload the list
    }

    suspend fun moveProductToBag(productId: Int) {
        if (model.currentUserId == -1) return
        // Remove from wishlist and add to current products
        model.removeFromWishList(model.currentUserId, productId, addToCurrent = true)
        loadWishlistProducts() // Reload the list
    }
}

