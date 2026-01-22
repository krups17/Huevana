package org.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.IconButton
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.materialIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.Persona
import domain.Product
import io.ktor.http.ContentType
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.example.domain.ColourAnalysisResult
import org.example.domain.Palette
import org.example.presentation.HomepageViewModel

@Composable
fun HomepageView(padding: PaddingValues, viewmodel: HomepageViewModel, onUpdateAnalysis: () -> Unit = {}) {
    var currentRecommendedProducts: MutableList<Int>? = mutableListOf()
    var currentFilteredProducts: MutableList<Int>? = mutableListOf()
    var sizeRecommendedProducts = 0
    var sizeFilteredProducts = 0
    var currentProduct = Product(1, "", "ordinary", "", 1,8.toFloat(),"")
    var currentProductId: Int? = 0
    var currentProductName = ""
    var currentPersona = Persona(id = 1, name = "Grandma", colourAnalysisResult = 1)
    var currentProductBrand = "o"
    var currentProductRating = 0.toFloat()
    var currentPersonaCAR = ColourAnalysisResult(0,"", "", "", "", 0, 0)
    var currentPersonaPalette = Palette(0, "")
    var currentPersonaPaletteName = "No Palette - Select Update Analysis"
    var currentHexCodeString = ""
    var currentHexCodeInt = 1
    var currentPersonaHexCodeStringList: MutableList<String>? = mutableListOf<String>()
    var hexCodesSize = 0

    var filter by remember { mutableStateOf("") }

    runBlocking {
        launch {
            currentPersona = viewmodel.fetch_current_persona_from_id()
            println("currentPersona.id(1): ${currentPersona.id}")
        }
    }
    if (currentPersona.colourAnalysisResult != null) {
        runBlocking {
            launch {
                println("currentPersona.id(2): ${currentPersona.id}")
                println("currentPersona.colourAnalysisResult(2): ${currentPersona.colourAnalysisResult}")
                currentPersonaCAR = viewmodel.fetch_CAR_from_id(currentPersona.colourAnalysisResult)
                println("currentPersonaCAR.id(2): ${currentPersonaCAR.id}")
            }
        }
        runBlocking {
            launch {
                println("currentPersonaCAR.id(3): ${currentPersonaCAR.id}")
                currentPersonaPalette = viewmodel.fetch_palette_from_id(currentPersonaCAR.palette)
                println("currentPersonaPalette.id(3): ${currentPersonaPalette.id}")
            }
        }
        currentPersonaPaletteName = currentPersonaPalette.name
    } else {
        currentPersonaPaletteName = "No Palette"
    }
    runBlocking {
        launch {
            currentPersonaHexCodeStringList = currentPersonaPalette.hexCodes
        }
    }

    runBlocking {
        launch {
            currentRecommendedProducts = currentPersona.recommendedProducts
            sizeRecommendedProducts = viewmodel.getSizeMutableList(currentRecommendedProducts)
            currentFilteredProducts = currentRecommendedProducts
            sizeFilteredProducts = sizeRecommendedProducts
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF9F7FB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Left sidebar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxHeight(0.5f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentPersona.profilepic != null) {
                        Box(
                            modifier = Modifier.size(250.dp)
                        ) {
                            Image(
                                painter = painterResource("${currentPersona.profilepic}.jpg"),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Person",
                            tint = Color(0xFFE95D7A),
                            modifier = Modifier.size(250.dp)
                        )
                    }
                }
                Text(
                    currentPersona.name,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp,
                )
                Text (
                    text = currentPersonaPaletteName,
                    color = Color(0xFF9A4C5F),
                    fontSize = 20.sp,
                )

                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    "My Palette",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Palette boxes
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier
                            .height(100.dp)
                            .width(300.dp)
                            .align(Alignment.Center)
                            .heightIn(max = 100.dp)
                            .border(width = 1.dp, color = Color(0xFFE95D7A))
                    ) {
                        hexCodesSize = currentPersonaHexCodeStringList?.size?:0
                        println("hexCodesSize: $hexCodesSize")
                        items(hexCodesSize) { index ->
                            HexCodeBox(currentPersonaHexCodeStringList?.get(index))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onUpdateAnalysis() },
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
            // Main page
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    "Recommended Products",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black,
                )
                //var search by remember { mutableStateOf("") }

                Spacer(modifier = Modifier.height(16.dp))

                var expanded by remember { mutableStateOf(false) }
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

                Spacer(modifier = Modifier.height(16.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        val stateGrid = rememberLazyGridState()

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(BorderStroke(2.dp, Color(0xFFE95D7A)))
                        ) {
                            if (currentPersona.colourAnalysisResult == null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    Alignment.Center,
                                )
                                {
                                    Text(
                                        "Please select Update Analysis to get started!",
                                        color = Color(0xFFE95D7A),)
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.Center)
                                        .heightIn(max = 520.dp),
                                    state = stateGrid,
                                ) {
                                    currentFilteredProducts = viewmodel.dropMenuFilter(filter, currentRecommendedProducts)
                                    sizeFilteredProducts = viewmodel.getSizeMutableList(currentFilteredProducts)
                                    items(sizeFilteredProducts) { index ->
                                        currentProductId = currentFilteredProducts?.get(index)
                                        runBlocking {
                                            launch {
                                                currentProduct = viewmodel.fetch_product_from_id(currentProductId?:0)
                                                currentProductBrand = currentProduct.brand
                                                currentProductRating = currentProduct.rating
                                                currentProductName = currentProduct.name
                                            }
                                        }
                                        ProductBox(currentProductName, currentProductRating, currentProductBrand, currentProduct.id.toString(), viewmodel) // CHANGE RATING
                                    }
                                }
                                VerticalScrollbar(
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                        .background(Color(0xFFF9F7FB)),
                                    adapter = rememberScrollbarAdapter(stateGrid)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductBox(name: String, rating: Float, brand: String, id: String, viewmodel: HomepageViewModel) {
    val productId = id.toIntOrNull() ?: 0
    var isInWishlist by remember(productId) { mutableStateOf(false) }

    // Update wishlist state when productId changes
    LaunchedEffect(productId) {
        isInWishlist = viewmodel.isProductInWishlist(productId)
    }

    Card(
        backgroundColor = Color(0xFFF9F7FB),
        border = BorderStroke(2.dp, Color(0xFFE95D7A)),
        modifier = Modifier
            .height(250.dp)
            .clickable {}  // add onclick in another ticket
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Add top padding to account for heart icon
                Spacer(modifier = Modifier.height(32.dp))

                // Square image container - fixed smaller size
                Card(
                    backgroundColor = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE95D7A)),
                    modifier = Modifier
                        .size(120.dp) // Fixed square size
                        .aspectRatio(1f) // Ensures it's square
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        SimpleImageHomepage(id)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Product info below the image - ensure it's visible
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$rating • $brand",
                        fontSize = 12.sp,
                        color = Color(0xFFE95D7A)
                    )
                }
            }

            // Heart icon in top-right corner of the card (outside image area)
            IconButton(
                onClick = {
                    runBlocking {
                        if (isInWishlist) {
                            viewmodel.removeProductFromWishlist(productId)
                        } else {
                            viewmodel.addProductToWishlist(productId)
                        }
                        // Re-check state after operation to ensure consistency
                        isInWishlist = viewmodel.isProductInWishlist(productId)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Add to wishlist",
                    tint = if (isInWishlist) Color(0xFFE95D7A) else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun HexCodeBox(hexCode: String?) {
    if (hexCode != null) {
        println("hexCode: $hexCode")
        var hexCodeString = "ff" +  hexCode
        var hexCodeInt = hexCodeString.hexToInt()
        println("hexCodeInt: $hexCodeInt")
//        var hexCodeColor = 0xff9ea0a2
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(hexCodeInt)),
            backgroundColor = (Color(hexCodeInt)),
            border = BorderStroke(2.dp, Color(hexCodeInt)),
        ) {

        }
    }
}

@Composable
fun SimpleImageHomepage(image: String?) {
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

