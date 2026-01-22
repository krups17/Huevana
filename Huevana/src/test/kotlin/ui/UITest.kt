package uitest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performKeyPress
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import domain.Model
import domain.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.example.data.MockStorage
import org.example.presentation.SignInViewModel
import org.example.presentation.WishlistPageViewModel
import org.example.ui.PersonaPageViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/*
 * From Kotlin Multiplatform documentation
 * https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-desktop-ui-testing.html
 */

class UITest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testToolBar(){
        rule.setContent {
            var homePage by remember { mutableStateOf("false")}
            var personaPage by remember { mutableStateOf("false")}
            var accountPage by remember { mutableStateOf("false")}
            var faceCapturePage by remember { mutableStateOf("false")}

            TopAppBar(
                title = { Text("Huevana") },
                backgroundColor = Color(0xFFB8B8FF),
                contentColor = Color.White,
                actions = {
                    IconButton(
                        onClick = { homePage = "true" },
                        modifier = Modifier.testTag("homeButton")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "View Home")
                    }
                    IconButton(
                        onClick = { personaPage = "true" },
                        modifier = Modifier.testTag("personaButton")
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "View Personas")
                    }
                    IconButton(
                        onClick = { accountPage = "true" },
                        modifier = Modifier.testTag("accountButton")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "View Account")
                    }
                    IconButton(
                        onClick = { faceCapturePage = "true" },
                        modifier = Modifier.testTag("faceCaptureButton")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Face Capture")
                    }
                }
            )
            Text(text= homePage, modifier = Modifier.testTag("homeText"))
            Text(personaPage, modifier = Modifier.testTag("personaText"))
            Text(accountPage, modifier = Modifier.testTag("accountText"))
            Text(faceCapturePage, modifier = Modifier.testTag("fcText"))
        }

        rule.onNodeWithTag("homeText").assertTextEquals("false")
        rule.onNodeWithTag("personaText").assertTextEquals("false")
        rule.onNodeWithTag("accountText").assertTextEquals("false")
        rule.onNodeWithTag("fcText").assertTextEquals("false")

        rule.onNodeWithTag("personaButton").performClick()

        rule.onNodeWithTag("homeText").assertTextEquals("false")
        rule.onNodeWithTag("personaText").assertTextEquals("true")
        rule.onNodeWithTag("accountText").assertTextEquals("false")
        rule.onNodeWithTag("fcText").assertTextEquals("false")

        rule.onNodeWithTag("accountButton").performClick()

        rule.onNodeWithTag("homeText").assertTextEquals("false")
        rule.onNodeWithTag("personaText").assertTextEquals("true")
        rule.onNodeWithTag("accountText").assertTextEquals("true")
        rule.onNodeWithTag("fcText").assertTextEquals("false")

        rule.onNodeWithTag("faceCaptureButton").performClick()

        rule.onNodeWithTag("homeText").assertTextEquals("false")
        rule.onNodeWithTag("personaText").assertTextEquals("true")
        rule.onNodeWithTag("accountText").assertTextEquals("true")
        rule.onNodeWithTag("fcText").assertTextEquals("true")

        rule.onNodeWithTag("homeButton").performClick()

        rule.onNodeWithTag("homeText").assertTextEquals("true")
        rule.onNodeWithTag("personaText").assertTextEquals("true")
        rule.onNodeWithTag("accountText").assertTextEquals("true")
        rule.onNodeWithTag("fcText").assertTextEquals("true")
    }

    @Test
    fun testSignInGood() = runBlocking {
        val storage = MockStorage()
        val model = Model(storage)
        storage.signUp_new_user("user", "password")

        val signInViewModel = SignInViewModel(model)
        signInViewModel.username.value = "user"
        signInViewModel.password.value = "password"

        val result = signInViewModel.signIn()

        assertTrue(result)
        assertTrue(signInViewModel.isSignedIn.value)
    }

    @Test
    fun testSignInBad() = runBlocking {
        val signInViewModel = SignInViewModel(Model(MockStorage()))

        signInViewModel.username.value = ""
        signInViewModel.password.value = ""

        val result = signInViewModel.signIn()

        val expectedMessage = "Please enter username and password"

        assertFalse(result)
        assertEquals(expectedMessage, signInViewModel.errorMessage.value)
        assertFalse(signInViewModel.isSignedIn.value)
    }

    @Test
    fun testAddPersona() = runBlocking {
        val storage = MockStorage()
        val personaViewModel = PersonaPageViewModel(Model(storage))
        storage.signUp_new_user("user", "password")

        personaViewModel.personaName.value = "name"
        personaViewModel.model.currentUserId = 1
        assertEquals(0, personaViewModel.personaList.size)

        personaViewModel.addPersona()

        assertEquals(1, personaViewModel.personaList.size)
        assertEquals("name", personaViewModel.personaList[0].name)
    }

    @Test
    fun testEditPersona() = runBlocking {
        val storage = MockStorage()
        val personaViewModel = PersonaPageViewModel(Model(storage))
        storage.signUp_new_user("user", "password")

        personaViewModel.personaName.value = "name"
        personaViewModel.model.currentUserId = 1

        personaViewModel.addPersona()
        personaViewModel.editPersonaName(1, "Name2")

        assertEquals(1, personaViewModel.personaList.size)
        assertEquals("Name2", personaViewModel.personaList[0].name)
    }


    @Test
    fun testHomepageFilter() {
        // testing Filter button
        rule.setContent {
            var expanded by remember { mutableStateOf(false) }
            var filter by remember { mutableStateOf("") }
            Box(
                modifier = Modifier
                    .width(300.dp),
                contentAlignment = Alignment.Center

            ) {
                Button(
                    onClick = { expanded = !expanded },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color(0xFFE95D7A)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Text(text = "Filter")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .width(300.dp)
                ) {
                    DropdownMenuItem(
                        //text = "Option 1",
                        onClick = {filter = "Lip"; expanded = false},
                    ) {
                        if (filter == "Lip") {
                            Text("Lipstick", color = Color(0xFFE95D7A))
                        } else {
                            Text("Lipstick")
                        }
                    }
                    DropdownMenuItem(
                        //text = "Option 2",
                        onClick = {filter = "Blush"; expanded = false}
                    ) {
                        if (filter == "Blush") {
                            Text("Blush", color = Color(0xFFE95D7A))
                        } else {
                            Text("Blush")
                        }
                    }
                    DropdownMenuItem(
                        //text = "Option 3",
                        onClick = {filter = "Eyeshadow"; expanded = false}
                    ) {
                        if (filter == "Eyeshadow") {
                            Text("Eyeshadow", color = Color(0xFFE95D7A))
                        } else {
                            Text("Eyeshadow")
                        }
                    }
                    DropdownMenuItem(
                        //text = "Option 4",
                        onClick = {filter = ""; expanded = false}
                    ) {
                        if (filter == "") {
                            Text("No Filter", color = Color(0xFFE95D7A))
                        } else {
                            Text("No Filter")
                        }
                    }
                }
            }
        }
        rule.onNodeWithText("Filter").isDisplayed()
        rule.onNodeWithText("Filter").performClick()
        rule.onNodeWithText("Lipstick").isDisplayed()
        rule.onNodeWithText("Blush").isDisplayed()
        rule.onNodeWithText("Eyeshadow").isDisplayed()
        rule.onNodeWithText("No Filter").isDisplayed()
    }

    @Test
    fun testHomepageUpdateAnalysisButton() {
        // testing Filter button
        rule.setContent {
            Button(
                onClick = {},
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE95D7A),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                Text("Update Analysis")
            }
        }
        rule.onNodeWithText("Update Analysis").isDisplayed()
        rule.onNodeWithText("Update Analysis").performClick()
        rule.onNodeWithText("Update Analysis").isDisplayed()
    }

    // ----------------------------
    // WISHLIST PAGE VIEWMODEL TESTS
    // ----------------------------

    private fun createModel(): Model = Model(MockStorage())

    // Helper function to add user both locally and to storage for tests
    private suspend fun Model.addUserToStorage(username: String, password: String, email: String, firstname: String, lastname: String): Int {
        addUser(username, password, email, firstname, lastname)
        val user = users.find { it?.email == email } ?: error("User not found after addUser")
        return add_user(user)
    }

    // Helper function to add product both locally and to storage for tests
    private fun Model.addProductToStorage(name: String, brand: String, imageUrl: String = "", palette: Int = 1, rating: Float = 0.0f, shade: String = ""): Product {
        addProduct(name, brand, imageUrl, palette, rating, shade)
        val product = products.find { it?.name == name && it?.brand == brand } ?: error("Product not found after addProduct")
        (storage as? MockStorage)?.seedProduct(product)
        return product
    }

    @Test
    fun loadWishlistProducts_Success() = runBlocking {
        val model = createModel()
        val userId = model.addUserToStorage(
            username = "user1",
            password = "pass1",
            email = "user1@email.com",
            firstname = "User",
            lastname = "One"
        )
        model.currentUserId = userId

        // Add products to both Model and storage
        model.addProductToStorage("Mascara", brand = "Ordinary")
        model.addProductToStorage("Lipstick", brand = "Maybelline")

        // Add products to wishlist
        model.addToWishList(userId, 1)
        model.addToWishList(userId, 2)

        val viewModel = WishlistPageViewModel(model)

        // Wait for async loading to complete
        delay(100)

        assertFalse(viewModel.isLoading.value)
        assertEquals(2, viewModel.wishlistProducts.value.size)
        assertEquals("Mascara", viewModel.wishlistProducts.value[0].name)
        assertEquals("Lipstick", viewModel.wishlistProducts.value[1].name)
    }

    @Test
    fun loadWishlistProducts_EmptyWishlist() = runBlocking {
        val model = createModel()
        val userId = model.addUserToStorage(
            username = "user1",
            password = "pass1",
            email = "user1@email.com",
            firstname = "User",
            lastname = "One"
        )
        model.currentUserId = userId

        val viewModel = WishlistPageViewModel(model)

        // Wait for async loading to complete
        delay(100)

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.wishlistProducts.value.isEmpty())
    }

    @Test
    fun loadWishlistProducts_UserNotLoggedIn() = runBlocking {
        val model = createModel()
        model.currentUserId = -1 // Not logged in

        val viewModel = WishlistPageViewModel(model)

        // Wait for async loading to complete
        delay(100)

        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.wishlistProducts.value.isEmpty())
    }

    @Test
    fun removeProductFromWishlist_Success() = runBlocking {
        val model = createModel()
        val userId = model.addUserToStorage(
            username = "user1",
            password = "pass1",
            email = "user1@email.com",
            firstname = "User",
            lastname = "One"
        )
        model.currentUserId = userId

        // Add products to both Model and storage
        model.addProductToStorage("Mascara", brand = "Ordinary")
        model.addProductToStorage("Lipstick", brand = "Maybelline")

        // Add products to wishlist
        model.addToWishList(userId, 1)
        model.addToWishList(userId, 2)

        val viewModel = WishlistPageViewModel(model)

        // Wait for initial load
        delay(100)
        assertEquals(2, viewModel.wishlistProducts.value.size)

        // Remove a product
        viewModel.removeProductFromWishlist(1)

        // Wait for reload
        delay(100)

        assertFalse(viewModel.isLoading.value)
        assertEquals(1, viewModel.wishlistProducts.value.size)
        assertEquals("Lipstick", viewModel.wishlistProducts.value[0].name)

        // Verify it was removed from model
        val user = model.fetch_user_from_id(userId)
        assertFalse(user.wishList.contains(1))
        assertTrue(user.wishList.contains(2))
    }
}