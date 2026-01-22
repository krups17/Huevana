package org.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import domain.Persona
import kotlinx.coroutines.launch
import org.example.domain.FESharedPersonas


@Composable
fun PersonasPageView(padding: PaddingValues, viewModel: PersonaPageViewModel, onPersonaClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color(0xFFF9F7FB))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            repeat(viewModel.columns) { rowIdx ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    repeat(3) { colIdx ->
                        val idx = rowIdx * 3 + colIdx
                        if (idx < viewModel.numPersona) {
                            // this is a Persona Box
                            PersonaBox(
                                persona = viewModel.personaList[idx],
                                modifier = Modifier.weight(1f),
                                viewModel,
                                onPersonaClick
                            )
                        } else if (idx == viewModel.numPersona) {
                            // this is a Plus Box
                            PlusBox(
                                viewModel,
                                modifier = Modifier.weight(1f),
                            )
                        } else if (idx - viewModel.numPersona - 1 < viewModel.numShared) {
                            // this is a Shared Box
                            BaseSharedBox(
                                sharedPersona = viewModel.sharedPersonaList[idx - viewModel.numPersona - 1],
                                modifier = Modifier.weight(1f),
                                viewModel,
                                onPersonaClick
                            )
                        } else {
                            // this is a Space Box
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PersonaBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel, onPersonaClick: () -> Unit) {
    if (persona.id == viewModel.selectedPersonaId.value) {
        if (viewModel.state.value == State.MENU) {
            MenuBox(persona, modifier, viewModel, onPersonaClick)
        } else if (viewModel.state.value == State.EDIT) {
            EditBox(persona, modifier, viewModel)
        } else if (viewModel.state.value == State.DELETE) {
            ConfirmDeleteBox(persona, modifier, viewModel)
        } else if (viewModel.state.value == State.ERROR) {
            ErrorBox(persona, modifier, viewModel)
        } else if (viewModel.state.value == State.SHARE) {
            ShareBox(persona, modifier, viewModel)
        } else {
            BasePersonaBox(persona, modifier, viewModel, onPersonaClick)
        }
    } else {
        BasePersonaBox(persona, modifier, viewModel, onPersonaClick)
    }
}

@Composable
fun BasePersonaBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel, onPersonaClick: () -> Unit) {
    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                viewModel.selectedPersonaId.value = persona.id
                viewModel.selectedSPId.value = -1
                viewModel.addingPersona.value = false
                viewModel.state.value = State.MENU
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (persona.profilepic != null) {
                Box(
                    modifier = Modifier.size(110.dp)
                ) {
                    Image(
                        painter = painterResource("${persona.profilepic}.jpg"),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "icon",
                    tint = Color.White,
                    modifier = Modifier.size(110.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = persona.name,
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
}


@Composable
fun MenuBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel, onPersonaClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val OPTIONS = listOf("Select", "Edit", "Make Default", "Share", "Delete")

    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                viewModel.state.value = State.BASE
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.model.currentPersonaId = persona.id
                            onPersonaClick()
                        }
                    },
                    border = ButtonDefaults.outlinedBorder,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Select", color = Color(0xFFE95D7A))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            repeat(2) { rowIdx ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) {colIdx ->
                        val idx = rowIdx * 2 + colIdx + 1  // ignore Select
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (OPTIONS[idx] == "Make Default") {
                                        viewModel.makeDefault(persona.id)
                                        viewModel.model.currentPersonaId = persona.id
                                        onPersonaClick()
                                    } else if (OPTIONS[idx] == "Edit") {
                                        viewModel.state.value = State.EDIT
                                    } else if (OPTIONS[idx] == "Delete") {
                                        viewModel.state.value = State.DELETE
                                    } else if (OPTIONS[idx] == "Share") {
                                        viewModel.state.value = State.SHARE
                                    }
                                }
                            },
                            border = ButtonDefaults.outlinedBorder,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(OPTIONS[idx], color = Color(0xFFE95D7A))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun EditBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val personaName = mutableStateOf(persona.name)
    val selectedProfilePic = mutableStateOf(persona.profilepic)
    val profilePicOptions = listOf(142L, 143L, 144L, 145L, 146L)

    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                viewModel.selectedPersonaId.value = persona.id
                viewModel.selectedSPId.value = -1
                viewModel.addingPersona.value = false
                viewModel.state.value = State.MENU
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = personaName.value,
                onValueChange = { personaName.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Profile picture selection
            Text("Select Profile Picture", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profilePicOptions.forEach { picId ->
                        val isSelected = selectedProfilePic.value == picId
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedProfilePic.value = picId
                                }
                        ) {
                            Image(
                                painter = painterResource("${picId}.jpg"),
                                contentDescription = "Profile Picture $picId",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        // Use combined function to avoid double fetch
                        viewModel.editPersonaNameAndProfilepic(persona.id, personaName.value, selectedProfilePic.value)
                        viewModel.selectedPersonaId.value = -1
                        viewModel.selectedSPId.value = -1
                        viewModel.addingPersona.value = false
                        viewModel.state.value = State.BASE
                    }
                },
                border = ButtonDefaults.outlinedBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save", color = Color(0xFFE95D7A))
            }
        }
    }
}


@Composable
fun ShareBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val receiverEmail = mutableStateOf("")
    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                viewModel.selectedPersonaId.value = persona.id
                viewModel.selectedSPId.value = -1
                viewModel.addingPersona.value = false
                viewModel.state.value = State.MENU
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = receiverEmail.value,
                onValueChange = { receiverEmail.value = it },
                label = { Text("Receiver Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.sharePersona(persona.id, receiverEmail.value)
                    }
                },
                border = ButtonDefaults.outlinedBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share", color = Color(0xFFE95D7A))
            }
        }
    }
}


@Composable
fun ConfirmDeleteBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                viewModel.selectedPersonaId.value = persona.id
                viewModel.selectedSPId.value = -1
                viewModel.addingPersona.value = false
                viewModel.state.value = State.MENU
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deletePersona(persona.id)
                    }
                },
                border = ButtonDefaults.outlinedBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Delete", color = Color(0xFFE95D7A))
            }
        }
    }
}


@Composable
fun ErrorBox(persona: Persona, modifier: Modifier, viewModel: PersonaPageViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                viewModel.selectedPersonaId.value = persona.id
                viewModel.selectedSPId.value = -1
                viewModel.addingPersona.value = false
                viewModel.state.value = State.MENU
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.selectedPersonaId.value = -1
                        viewModel.selectedSPId.value = -1
                        viewModel.addingPersona.value = false
                        viewModel.state.value = State.BASE
                    }
                },
                border = ButtonDefaults.outlinedBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(viewModel.errorMessage.value, color = Color(0xFFE95D7A))
            }
        }
    }
}

@Composable
fun PlusBox(
    viewModel: PersonaPageViewModel,
    modifier: Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val selectedProfilePic = mutableStateOf<Long?>(null)
    val profilePicOptions = listOf(142L, 143L, 144L, 145L, 146L)

    Card(
        backgroundColor = Color(0xFFE95D7A),
        modifier = modifier
            .height(240.dp)
            .clickable {
                if (viewModel.addingPersona.value) {
                    viewModel.addingPersona.value = false
                } else {
                    viewModel.addingPersona.value = true
                    viewModel.selectedSPId.value = -1
                    viewModel.selectedPersonaId.value = -1
                }
            }
    ) {
        if (!viewModel.addingPersona.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Plus Icon",
                    tint = Color.White,
                    modifier = Modifier.size(110.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Add Persona",
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.personaName.value,
                    onValueChange = { viewModel.personaName.value = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Profile picture selection
                Text("Select Profile Picture", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        profilePicOptions.forEach { picId ->
                            val isSelected = selectedProfilePic.value == picId
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selectedProfilePic.value = picId
                                    }
                            ) {
                                Image(
                                    painter = painterResource("${picId}.jpg"),
                                    contentDescription = "Profile Picture $picId",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.addPersona(selectedProfilePic.value)
                            selectedProfilePic.value = null
                        }},
                    enabled = viewModel.personaName.value.isNotBlank(),
                    border = ButtonDefaults.outlinedBorder,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Persona", color = Color(0xFFE95D7A))
                }
            }
        }
    }
}


@Composable
fun BaseSharedBox(sharedPersona: FESharedPersonas, modifier: Modifier, viewModel: PersonaPageViewModel, onPersonaClick: () -> Unit) {
    Card(
        backgroundColor = Color(0xFFEFA8B7),
        modifier = modifier
            .height(240.dp)
            .clickable {
                if (viewModel.state.value == State.SHARE && viewModel.selectedSPId.value == sharedPersona.SPid) {
                    viewModel.selectedSPId.value = -1
                    viewModel.state.value = State.BASE
                } else {
                    viewModel.selectedSPId.value = sharedPersona.SPid
                    viewModel.selectedPersonaId.value = -1
                    viewModel.addingPersona.value = false
                    viewModel.state.value = State.SHARE
                }
            }
    ) {
        val coroutineScope = rememberCoroutineScope()
        val OPTIONS = listOf("Accept", "Reject")

        if (viewModel.state.value == State.SHARE && sharedPersona.SPid == viewModel.selectedSPId.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {},
                        border = ButtonDefaults.outlinedBorder,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${sharedPersona.owner} shared a Persona with you", color = Color(0xFFE95D7A))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) {idx ->
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (OPTIONS[idx] == "Accept") {
                                        viewModel.acceptPersona(sharedPersona.personaId, sharedPersona.SPid)
                                    } else {
                                        viewModel.rejectPersona(sharedPersona.personaId, sharedPersona.SPid)
                                    }
                                }
                            },
                            border = ButtonDefaults.outlinedBorder,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(OPTIONS[idx], color = Color(0xFFE95D7A))
                        }
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                if (sharedPersona.profilepic != null) {
                    Box(
                        modifier = Modifier.size(110.dp)
                    ) {
                        // Grey out the image using ColorFilter
                        val colorMatrix = ColorMatrix().apply {
                            setToSaturation(0f) // 0 = grayscale
                        }
                        Image(
                            painter = painterResource("${sharedPersona.profilepic}.jpg"),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.colorMatrix(colorMatrix),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(110.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = sharedPersona.persona,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}