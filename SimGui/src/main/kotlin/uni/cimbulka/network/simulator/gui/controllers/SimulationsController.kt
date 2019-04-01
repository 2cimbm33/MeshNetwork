package uni.cimbulka.network.simulator.gui.controllers

import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property
import uni.cimbulka.network.simulator.gui.database.SimulationDao
import uni.cimbulka.network.simulator.gui.events.CloseEvent
import uni.cimbulka.network.simulator.gui.views.SimulationsView

class SimulationsController : Controller() {
    private val mainController: MainController by inject()
    private val dao: SimulationDao by inject()

    val simulations: ObservableList<String>
        get() = dao.simulationsList

    var selected: String? by property()
    fun selectedProperty() = getProperty(SimulationsController::selected)

    var disabled: Boolean by property(true)
    fun disabledProperty() = getProperty(SimulationsController::disabled)

    init {
        dao.getSimulations()
    }

    fun handleSelectionChanged(item: String?) {
            selected = item
            disabled = item == null || item.isEmpty()
    }

    fun handleOpenClicked() {
        selected?.let {
            mainController.simId = it
            fire(CloseEvent<SimulationsView>())
        }
    }

    fun handleCloseClicked() {
        fire(CloseEvent<SimulationsView>())
    }
}