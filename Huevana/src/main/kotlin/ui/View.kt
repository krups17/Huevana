package org.example.ui

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import org.example.presentation.SignInViewModel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageAccounts
import desktop.presentation.ViewModel
import domain.Persona
import domain.User
import kotlinx.coroutines.launch
import org.example.presentation.AccountPageViewModel
import org.example.presentation.ChatbotPageViewModel
import org.example.presentation.FaceCaptureViewModel
import org.example.presentation.HomepageViewModel
import org.example.presentation.NotificationsViewModel
import org.example.presentation.SignUpViewModel
import org.example.presentation.WishlistPageViewModel

@Composable
fun View(viewModel: ViewModel) {
    val scaffoldState = rememberScaffoldState()
    var page by remember { mutableStateOf("signin")}

    val notificationsViewModel = remember { NotificationsViewModel(viewModel.model) }

    // fetch current user when logged in
    var currentUser by remember { mutableStateOf<User?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.model.currentUserId) {
        if (viewModel.model.currentUserId != -1) {
            currentUser = viewModel.model.fetch_user_from_id(viewModel.model.currentUserId)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (page != "signin" && page != "signup" && currentUser != null) {
                Toolbar(
                    viewHomepage = { page = "homepage" },
                    viewChatbot = { page = "chatbot" },
                    viewPersonas = { page = "personas" },
                    viewAccount = { page = "account" },
                    viewWishlist = { page = "wishlist" },
                    viewFaceCapture = { page = "facecapture"},
                    notificationsViewModel = notificationsViewModel,
                    currentUser = currentUser!!
                )
            }
        },
        bottomBar = { },
    ) { padding ->
        if (page == "signin") {
            val signInVM = remember { SignInViewModel(viewModel.model) }
            SignInView(
                viewModel = signInVM,
                onSignedIn = {
                    scope.launch {
                        // Refresh current user after sign in
                        currentUser = viewModel.model.fetch_user_from_id(viewModel.model.currentUserId)
                        page = "homepage"
                    }
                },
                onCreateAccount = { page = "signup" }
            )
        }
        if (page == "signup") {
            val signUpVM = remember { SignUpViewModel(viewModel.model) }
            SignUpView(
                viewModel = signUpVM,
                onSignUp = {
                    scope.launch {
                        // Refresh current user after sign up
                        currentUser = viewModel.model.fetch_user_from_id(viewModel.model.currentUserId)
                        page = "homepage"
                    }
                },
                onSignInClick = { page = "signin" },
            )
        }
        if (page == "homepage") {
            HomepageView(padding, HomepageViewModel(viewModel.model), onUpdateAnalysis = { page = "facecapture" })
        }
        if (page == "chatbot") {
            ChatbotPageView(padding, ChatbotPageViewModel(viewModel.model))
        }
        if (page == "account") {
            AccountPageView(
                padding = padding,
                viewModel = AccountPageViewModel(viewModel.model),
                onLogout = {
                    currentUser = null
                    page = "signin"
                }
            )
        }
        if (page == "personas") {
            PersonasPageView(
                padding,
                PersonaPageViewModel(viewModel.model),
                onPersonaClick = { page = "homepage" },)
        }
        if (page == "facecapture") {
            FaceCapturePageView(
                padding, FaceCaptureViewModel(viewModel.model),
                personaId = viewModel.model.currentPersonaId
            )
        }
        if (page == "wishlist") {
            val wishlistVM = remember { WishlistPageViewModel(viewModel.model) }
            LaunchedEffect(page) {
                if (page == "wishlist") {
                    wishlistVM.loadWishlistProducts()
                }
            }
            WishlistPageView(padding, wishlistVM, onNavigateToHomepage = { page = "homepage" })
        }
    }
}

@Composable
fun Toolbar(
    viewHomepage: () -> Unit,
    viewChatbot: () -> Unit,
    viewPersonas: () -> Unit,
    viewAccount: () -> Unit,
    viewWishlist: () -> Unit,
    viewFaceCapture: () -> Unit,
    notificationsViewModel: NotificationsViewModel,
    currentUser: User
) {
    TopAppBar(
        title = { Text("Huevana") },
        backgroundColor = Color(0xFFE95D7A),
        contentColor = Color.White,
        actions = {
            IconButton(
                onClick = { viewHomepage() }) {
                Icon(Icons.Default.Home, contentDescription = "View Home Page")
            }
            IconButton(
                onClick = { viewChatbot() }) {
                Icon(Icons.Default.Forum, contentDescription = "Chat")
            }
            IconButton(
                onClick = { viewPersonas() }) {
                Icon(Icons.Default.Groups, contentDescription = "View Personas")
            }
            IconButton(
                onClick = { viewAccount() }) {
                Icon(Icons.Default.ManageAccounts, contentDescription = "View Account")
            }
            IconButton(
                onClick = { viewWishlist() }) {
                Icon(Icons.Default.Favorite, contentDescription = "View Wishlist")
            }
            IconButton(
                onClick = { viewFaceCapture() }) {
                Icon(Icons.Default.Face, contentDescription = "Face Capture")
            }
            Notifications(
                currentUser = currentUser,
                viewModel = notificationsViewModel
            )
        }
    )
}