package desktop.presentation

import domain.Model

class ViewModel(val model: Model) {
    // var tasks = mutableStateListOf<Task>()

    init {
        println("ViewModel: init")
        // model.subscribe(this)
    }

    // notifyChanges
}
