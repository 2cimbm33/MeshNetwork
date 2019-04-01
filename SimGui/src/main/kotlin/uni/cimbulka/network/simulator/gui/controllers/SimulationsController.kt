package uni.cimbulka.network.simulator.gui.controllers

import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property
import uni.cimbulka.network.simulator.gui.database.SimulationDao
import uni.cimbulka.network.simulator.gui.events.CloseEvent
import uni.cimbulka.network.simulator.gui.models.Simulation
import uni.cimbulka.network.simulator.gui.views.SimulationsView

class SimulationsController : Controller() {
    private val mainController: MainController by inject()
    private val dao: SimulationDao by inject()

    val simulations: ObservableList<Simulation>
        get() = dao.simulationsList

    var selected: Simulation? by property(value = null)
    fun selectedProperty() = getProperty(SimulationsController::selected)

    var disabled: Boolean by property(true)
    fun disabledProperty() = getProperty(SimulationsController::disabled)

    init {
        dao.getSimulations()
    }

    fun handleSelectionChanged(item: Simulation?) {
            selected = item
            disabled = item == null
    }

    fun handleOpenClicked() {
        selected?.let {
            mainController.simId = it.id
            fire(CloseEvent<SimulationsView>())
        }
    }

    fun handleCloseClicked() {
        fire(CloseEvent<SimulationsView>())
    }
}