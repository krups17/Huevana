package org.example.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import domain.Model
import domain.Persona
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.domain.FESharedPersonas
import kotlin.math.ceil

enum class State {
    BASE,
    MENU,
    EDIT,
    SHARE,
    DELETE,
    ERROR
}

class PersonaPageViewModel(val model: Model) {
    // for plus block
    var addingPersona = mutableStateOf(false)
    var personaName = mutableStateOf("")

    // for persona block
    var state = mutableStateOf(State.BASE)
    var selectedPersonaId = mutableStateOf(-1)
    var selectedSPId = mutableStateOf(-1)
    var errorMessage = mutableStateOf("")

    var personaList by mutableStateOf(listOf<Persona>())
    var sharedPersonaList by mutableStateOf(listOf<FESharedPersonas>())

    // for calculations:
    val numPersona: Int get() = personaList.size
    val numShared: Int get() = sharedPersonaList.size
    val blocks: Int get() = numPersona + 1 + numShared
    val columns: Int get() = ceil(blocks / 3.0).toInt()

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            fetch()
        }
    }

    suspend fun fetch() {
        repeat(5) {
            try {
                val user = model.fetch_user_from_id(model.currentUserId)
                val personaIds = user.personas
                val sharedPersonaIds = user.sharedPersonas ?: emptyList()
                val personas = model.fetch_persona_from_ids(personaIds)
                val sharedPersonas = model.fetch_shared_persona_from_ids(sharedPersonaIds)

                // Preserve the order of personas based on personaIds list
                val personaMap = personas.associateBy { it.id }
                personaList = personaIds.mapNotNull { id -> personaMap[id] }
                sharedPersonaList = sharedPersonas.map { it }
                return
            } catch (e: Exception) {
                println("Fetch failed, retrying: $e")
                delay(150L)
            }
        }
    }

    suspend fun addPersona(profilepic: Long? = null) {
        // Don't create persona if name is empty
        if (personaName.value.isBlank()) {
            return
        }

        addingPersona.value = false

        val personaId = model.add_persona(
            Persona(
                id = 0,
                name = personaName.value,
                recommendedProducts = null,
                colourAnalysisResult = null,
                profilepic = profilepic
            )
        )
        personaList += Persona(
            id = personaId,
            name = personaName.value,
            recommendedProducts = null,
            colourAnalysisResult = null,
            profilepic = profilepic
        )
        model.add_persona_to_user(model.currentUserId, personaId)

        personaName.value = ""
    }

    suspend fun editPersonaName(personaId: Int, newName: String) {
        model.update_persona_name(personaId, newName, model.currentUserId)
        // Update local persona list immediately to preserve position
        personaList = personaList.map {
            if (it.id == personaId) it.copy(name = newName) else it
        }
        // Fetch in background to sync with database
        fetch()
    }

    suspend fun editPersonaProfilepic(personaId: Int, newProfilepic: Long?) {
        model.update_persona_profilepic(personaId, newProfilepic)
        // Update local persona list immediately to preserve position
        personaList = personaList.map {
            if (it.id == personaId) it.copy(profilepic = newProfilepic) else it
        }
        // Fetch in background to sync with database
        fetch()
    }

    suspend fun editPersonaNameAndProfilepic(personaId: Int, newName: String, newProfilepic: Long?) {
        model.update_persona_name(personaId, newName, model.currentUserId)
        model.update_persona_profilepic(personaId, newProfilepic)
        // Update local persona list immediately to preserve position
        personaList = personaList.map {
            if (it.id == personaId) it.copy(name = newName, profilepic = newProfilepic) else it
        }
        // Fetch once in background to sync with database
        fetch()
    }

    suspend fun deletePersona(personaId: Int) {
        val msg = model.remove_persona(personaId, model.currentUserId)
        if (msg.contains("Error")) {
            errorMessage.value = msg
            state.value = State.ERROR
        } else {
            state.value = State.BASE
            scope.launch { fetch() }
        }
    }

    suspend fun sharePersona(personaId: Int, receiverEmail: String) {
        val receiver = model.fetch_user_from_email(receiverEmail)
        if (receiver == null) {
            errorMessage.value = "Error: Couldn't find user"
            state.value = State.ERROR
        } else {
            model.share_persona(model.currentUserId, receiver.id, personaId)
            errorMessage.value = "Successfully sent share request"
            state.value = State.ERROR
        }
    }

    suspend fun makeDefault(personaId: Int) {
        model.update_user_default_persona(model.currentUserId, personaId)
    }

    suspend fun acceptPersona(personaId: Int, SPId: Int) {
        model.accept_persona(personaId, SPId, model.currentUserId)
        scope.launch { fetch() }
    }

    suspend fun rejectPersona(personaId: Int, SPId: Int) {
        model.reject_persona(personaId, SPId, model.currentUserId)
        scope.launch { fetch() }
    }
}