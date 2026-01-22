package domain

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.data.MockStorage
import org.example.domain.ColourAnalysisResult
import org.example.domain.Palette
import org.example.presentation.HomepageViewModel
import kotlin.test.Test

class ViewModelTest {

    // HomePageViewModel Tests
    @Test
    fun init() {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        assert(viewModel.model == model)
    }

    @Test
    fun fetch_persona_from_id() = runBlocking {
        var persona = Persona(1, "Mom", mutableListOf(1), 1)
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        val id = model.add_persona(persona)
        var result = viewModel.fetch_persona_from_id(id)
        assert(result.id == id)
        assert(result.name == "Mom")
        assert(result.recommendedProducts?.size == 1)
        assert(result.colourAnalysisResult == 1)
    }

    @Test
    fun fetch_current_persona_from_id() = runBlocking{
        var persona = Persona(1, "Mom", mutableListOf(1), 1)
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        val id = model.add_persona(persona)
        model.currentPersonaId = 1
        val result =  viewModel.fetch_persona_from_id(model.currentPersonaId)
        assert(result.id == id)
        assert(result.name == "Mom")
        assert(result.recommendedProducts?.size == 1)
        assert(result.colourAnalysisResult == 1)
    }

    @Test
    fun getSizeMutableList () {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var list: MutableList<Int>? = mutableListOf(1, 2, 3, 4, 7, 2, 4)
        var result = viewModel.getSizeMutableList(list)
        assert(result == 7)
    }

    @Test
    fun fetch_product_from_id() = runBlocking {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var product = Product(1, "Blush", "Ordinary", "http://blush.com/blush", 1, 3.toFloat(), "")
        storage.products.add(product)
        var result = viewModel.fetch_product_from_id(1)
        assert(result.id == 1)
        assert(result.name == "Blush")
        assert(result.brand == "Ordinary")
        assert(result.imageUrl == "http://blush.com/blush")
        assert(result.palette == 1)
        assert(result.rating == 3.toFloat())
    }

    @Test
    fun fetch_palette_from_id() = runBlocking {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var palette = Palette(1,"Cool Summer")
        storage.palettes.add(palette)
        var result = viewModel.fetch_palette_from_id(1)
        assert(result.id == 1)
        assert(result.name == "Cool Summer")
    }

    @Test
    fun fetch_CAR_from_id() = runBlocking {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var CAR = ColourAnalysisResult(1, "light", "pink", "silver", "grey", 1, 1)
        storage.colourAnalysisResults.add(CAR)
        var result = viewModel.fetch_CAR_from_id(1)
        assert(result.id == 1)
        assert(result.undertone == "light")
        assert(result.value == "pink")
        assert(result.contrast == "silver")
        assert(result.chroma == "grey")
        assert(result.extractedColours == 1)
        assert(result.palette == 1)
    }

    @Test
    fun addProductToWishlist() = runBlocking{
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var productId = 1
        model.currentUserId = 1
        var user = User(1, "user", "pass", "email", "fn", "ln",
            mutableListOf(), mutableListOf(),mutableListOf(),mutableListOf())
        storage.add_user(user)
        model.users.add(user)
        assert(user.wishList.size == 0)
        var result = viewModel.addProductToWishlist(productId)
        assert(user.wishList.size == 1)
    }

    @Test
    fun removeProductFromWishlist() = runBlocking{
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var productId = 1
        model.currentUserId = 1
        var user = User(1, "user", "pass", "email", "fn", "ln",
            mutableListOf(), mutableListOf(),mutableListOf(),mutableListOf(1))
        storage.add_user(user)
        model.users.add(user)
        assert(user.wishList.size == 1)
        var result = viewModel.removeProductFromWishlist(productId)
        assert(user.wishList.size == 0)
    }

    @Test
    fun isProductInWishlist() = runBlocking {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        var productId = 1
        var user = User(1, "user", "pass", "email", "fn", "ln",
            mutableListOf(), mutableListOf(),mutableListOf(),mutableListOf(1))
        model.currentUserId = 1
        model.users.add(user)
        var result = viewModel.isProductInWishlist(productId)
        assert(result == true)
        model.users.get(0)?.wishList?.remove(1)
        result = viewModel.isProductInWishlist(productId)
        assert(result == false)
    }

    @Test
    fun dropMenuFilter() {
        var storage = MockStorage()
        var model = Model(storage)
        var viewModel = HomepageViewModel(model)
        storage.products.add(Product(1, "Blush", "Ordinary", "http://blush.com/blush", 1, 3.toFloat(), ""))
        storage.products.add(Product(2, "Lipstick", "Ordinary", "http://blush.com/blush", 1, 3.toFloat(), ""))
        var currentRecommendedProducts = mutableListOf(1,2)
        val filter = "Blush"
        var result = viewModel.dropMenuFilter(filter,currentRecommendedProducts)
        assert(result.size == 1)

    }

}