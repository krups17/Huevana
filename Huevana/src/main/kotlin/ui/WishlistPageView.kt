package org.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.presentation.WishlistPageViewModel
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.res.painterResource

@Composable
fun WishlistPageView(padding: PaddingValues, viewModel: WishlistPageViewModel, onNavigateToHomepage: () -> Unit = {}) {
    val wishlistProducts by viewModel.wishlistProducts
    val isLoading by viewModel.isLoading

    // Reload wishlist when this composable is displayed
    LaunchedEffect(Unit) {
        viewModel.loadWishlistProducts()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF9F7FB))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFFE95D7A)
            )
        } else if (wishlistProducts.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your wishlist is empty",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Start adding products to your wishlist to keep track of your favorite items.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onNavigateToHomepage() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFE95D7A),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Browse more Recommendations")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Title
                Text(
                    text = "Wishlist",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Scrollable product list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(wishlistProducts) { product ->
                        WishlistProductItem(product, viewModel)
                    }

                    // Browse more Recommendations button at the bottom - aligned with containers
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { onNavigateToHomepage() },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFE95D7A),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .widthIn(max = 700.dp) // Match container width
                                    .fillMaxWidth() // Fill available width up to max
                            ) {
                                Text("Browse more Recommendations", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistProductItem(product: domain.Product, viewModel: WishlistPageViewModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center // Center the containers
    ) {
        Card(
            backgroundColor = Color(0xFFF9F7FB),
            border = BorderStroke(1.dp, Color(0xFFE95D7A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .widthIn(max = 700.dp) // Limit max width to prevent stretching
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side: Product info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Product name
                        Text(
                            text = product.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating
                        Text(
                            text = "${product.rating}",
                            fontSize = 14.sp,
                            color = Color(0xFFE95D7A),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Brand
                        Text(
                            text = product.brand,
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Shade
                        Text(
                            text = product.shade,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remove button - aligned with text (not full width)
                    Button(
                        onClick = {
                            runBlocking {
                                viewModel.removeProductFromWishlist(product.id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFE95D7A).copy(alpha = 0.2f),
                            contentColor = Color(0xFFE95D7A)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                            disabledElevation = 0.dp
                        )
                        // No fillMaxWidth - button will size to content and align with text
                    ) {
                        Text("Remove", fontSize = 14.sp)
                    }
                }

                // Right side: Product image - bigger size
                Card(
                    backgroundColor = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE95D7A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .size(200.dp) // Increased to 200.dp for bigger picture
                        .aspectRatio(1f)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SimpleImageWishlist(product.id.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleImageWishlist(image: String?) {
    var imagejpg = image + ".jpg"
    Image(
        painter = painterResource(imagejpg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(8.dp))
    )
}
