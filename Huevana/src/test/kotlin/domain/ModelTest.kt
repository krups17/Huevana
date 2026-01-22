package domain

import org.example.data.MockStorage
import kotlin.test.Test
import kotlinx.coroutines.runBlocking

private fun createModel(): Model = Model(MockStorage())

private fun MutableList<Int>?.firstId(): Int? = this?.firstOrNull()

// Helper function to add user both locally and to storage for tests that use suspend functions
private suspend fun Model.addUserToStorage(username: String, password: String, email: String, firstname: String, lastname: String): Int {
    addUser(username, password, email, firstname, lastname)
    val user = users.find { it?.email == email } ?: error("User not found after addUser")
    return add_user(user)
}

class ModelTest {
    @Test
    fun init() {
        val model = createModel()
        assert(model.users.isEmpty())
        assert(model.products.isEmpty())
    }

    // --------------------------
    // TESTS RELATED TO USER
    // --------------------------

    @Test
    fun addUser() {
        val model = createModel()
        assert(model.users.isEmpty())
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        assert(model.users.size == 1)
        assert(model.users.isNotEmpty())
        assert(model.users.get(0)?.id == 1)
        model.addUser(username = "second user", password = "password2", email = "second_user@email.com",
            firstname = "second", lastname = "user")
        assert(model.users.size == 2)
        assert(model.users.get(1)?.id == 2)
    }

    @Test
    fun delUser() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        model.addUser(username = "second user", password = "password2", email = "second_user@email.com",
            firstname = "second", lastname = "user")
        model.addUser(username = "third user", password = "password2", email = "third_user@email.com",
            firstname = "third", lastname = "user")
        assert(model.users.size == 3)
        model.delUser(2)
        assert(model.users.size == 2)
        assert(model.users.get(0)?.id == 1)
        assert(model.users.get(1)?.id == 3)
    }

    @Test
    fun updatePassword() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        model.updatePassword(1, "password1", newPassword = "new_password1")
        assert(model.users.get(0)?.password == "new_password1")
    }

    @Test
    fun updateUsername() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        model.updateUsername(1, "new first user")
        assert(model.users.get(0)?.username == "new first user")
    }

    @Test
    fun updateEmail() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        model.updateEmail(1, "newfirst_user@email.com")
        assert(model.users.get(0)?.email == "newfirst_user@email.com")
    }

    @Test
    fun updateFirstName() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        model.updateFirstName(1, "new first user")
        assert(model.users.get(0)?.firstname == "new first user")
    }

    @Test
    fun updateLastName() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        model.updateLastName(1, "new user")
        assert(model.users.get(0)?.lastname == "new user")
    }

    @Test
    fun addFriend() {
        val model = createModel()
        model.addUser(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addUser(username = "user2", password = "pass2", email = "user2@email.com",
            firstname = "User", lastname = "Two")

        assert(model.users[0]?.friends?.isEmpty() ?: false)
        model.addFriend(1, 2)
        assert(model.users[0]?.friends?.size == 1)
        assert(model.users[0]?.friends?.firstId() == 2)
    }

    @Test
    fun removeFriend() {
        val model = createModel()
        model.addUser(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addUser(username = "user2", password = "pass2", email = "user2@email.com",
            firstname = "User", lastname = "Two")
        model.addUser(username = "user3", password = "pass3", email = "user3@email.com",
            firstname = "User", lastname = "Three")

        model.addFriend(1, 2)
        model.addFriend(1, 3)
        assert(model.users[0]?.friends?.size == 2)

        model.removeFriend(1, 2)
        assert(model.users[0]?.friends?.size == 1)
        assert(model.users[0]?.friends?.firstId() == 3)
    }

    @Test
    fun addToWishList() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", brand = "Ordinary")

        assert(model.users[0]?.wishList?.isEmpty() ?: false)
        model.addToWishList(1, 1)
        assert(model.users[0]?.wishList?.size == 1)
        assert(model.users[0]?.wishList?.firstId() == 1)
    }

    @Test
    fun removeFromWishList() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")
        model.addProduct("Lipstick", "Ordinary")

        model.addToWishList(1, 1)
        model.addToWishList(1, 2)
        assert(model.users[0]?.wishList?.size == 2)

        model.removeFromWishList(1, 1)
        assert(model.users[0]?.wishList?.size == 1)
        assert(model.users[0]?.wishList?.firstId() == 2)
    }

    @Test
    fun removeFromWishListAddToCurrent() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        model.addToWishList(1, 1)
        assert(model.users[0]?.currentProducts?.isEmpty() ?: false)

        model.removeFromWishList(1, 1, addToCurrent = true)
        assert(model.users[0]?.wishList?.isEmpty() ?: false)
        assert(model.users[0]?.currentProducts?.size == 1)
        assert(model.users[0]?.currentProducts?.firstId() == 1)
    }

    @Test
    fun addToCurrentProducts() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        assert(model.users[0]?.currentProducts?.isEmpty() ?: false)
        model.addToCurrentProducts(1, 1)
        assert(model.users[0]?.currentProducts?.size == 1)
        assert(model.users[0]?.currentProducts?.firstId() == 1)
    }

    @Test
    fun addToCurrentProductsRemovesFromWishList() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        model.addToWishList(1, 1)
        assert(model.users[0]?.wishList?.size == 1)

        model.addToCurrentProducts(1, 1)
        assert(model.users[0]?.wishList?.isEmpty() ?: false)
        assert(model.users[0]?.currentProducts?.size == 1)
    }

    @Test
    fun removeFromCurrentProducts() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        model.addToCurrentProducts(1, 1)
        assert(model.users[0]?.currentProducts?.size == 1)

        model.removeFromCurrentProducts(1, 1)
        assert(model.users[0]?.currentProducts?.isEmpty() ?: false)
    }

    @Test
    fun removeFromCurrentProductsAddToPast() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        model.addToCurrentProducts(1, 1)
        assert(model.users[0]?.pastProducts?.isEmpty() ?: false)

        model.removeFromCurrentProducts(1, 1, addToPast = true)
        assert(model.users[0]?.currentProducts?.isEmpty() ?: false)
        assert(model.users[0]?.pastProducts?.size == 1)
        assert(model.users[0]?.pastProducts?.firstId() == 1)
    }

    @Test
    fun addToPastProducts() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        assert(model.users[0]?.pastProducts?.isEmpty() ?: false)
        model.addToPastProducts(1, 1)
        assert(model.users[0]?.pastProducts?.size == 1)
        assert(model.users[0]?.pastProducts?.firstId() == 1)
    }

    @Test
    fun addToPastProductsRemovesFromCurrent() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        model.addToCurrentProducts(1, 1)
        assert(model.users[0]?.currentProducts?.size == 1)

        model.addToPastProducts(1, 1)
        assert(model.users[0]?.currentProducts?.isEmpty() ?: false)
        assert(model.users[0]?.pastProducts?.size == 1)
    }

    @Test
    fun removeFromPastProducts() = runBlocking {
        val model = createModel()
        model.addUserToStorage(username = "user1", password = "pass1", email = "user1@email.com",
            firstname = "User", lastname = "One")
        model.addProduct("Mascara", "Ordinary")

        model.addToPastProducts(1, 1)
        assert(model.users[0]?.pastProducts?.size == 1)

        model.removeFromPastProducts(1, 1)
        assert(model.users[0]?.pastProducts?.isEmpty() ?: false)
    }

    // ----------------------------
    // TESTS RELATED TO PERSONA
    // ----------------------------

    @Test
    fun addPersona() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        assert(model.users.get(0)?.personas?.isEmpty() ?: false)
        model.addPersona(1, "profile1")
        assert(model.users.get(0)?.personas?.size == 1)
    }

    @Test
    fun delPersona() {
        val model = createModel()
        model.addUser(username = "first user", password = "password1", email = "first_user@email.com",
            firstname = "first", lastname = "user")
        assert(model.users.get(0)?.personas?.isEmpty() ?: false)
        model.addPersona(1, "profile1")
        model.addPersona(1, "profile2")
        model.addPersona(1, "profile3")
        assert(model.users.get(0)?.personas?.size == 3)
        model.delPersona(1, 2)
        assert(model.users.get(0)?.personas?.size == 2)
        val personas = model.users.get(0)?.personas
        assert(personas?.firstOrNull() == 1)
        assert(personas?.getOrNull(1) == 3)
    }

    // ----------------------------
    // FUNCTIONS RELATED TO PRODUCT
    // ----------------------------

    @Test
    fun addProduct() {
        val model = createModel()
        assert(model.products.isEmpty())
        model.addProduct("Mascara", brand = "Ordinary")
        assert(model.products.size == 1)
    }

    @Test
    fun delProduct() {
        val model = createModel()
        assert(model.products.isEmpty())
        model.addProduct("Mascara", brand = "Ordinary")
        assert(model.products.size == 1)
    }

    @Test
    fun updateProductName() {
        val model = createModel()
        model.addProduct("Mascara", brand = "Ordinary")
        model.updateProductName(1, "New Mascara")
        assert(model.products.get(0)?.name == "New Mascara")
    }

    @Test
    fun updateProductBrand() {
        val model = createModel()
        model.addProduct("Mascara", brand = "Ordinary")
        model.updateProductBrand(1, "Not Ordinary")
        assert(model.products.get(0)?.brand == "Not Ordinary")
    }

    // Note: updateProductTutVid, updateProductStoreRatings, and updateProductHex tests removed
    // because these fields no longer exist in the Product data class
}