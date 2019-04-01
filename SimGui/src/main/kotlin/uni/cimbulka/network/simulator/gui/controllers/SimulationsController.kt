package uni.cimbulka.network.simulator.gui.controllers

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property
import uni.cimbulka.network.simulator.gui.Database
import uni.cimbulka.network.simulator.gui.events.CloseEvent
import uni.cimbulka.network.simulator.gui.views.SimulationsView

class SimulationsController : Controller() {
    private val mainController: MainController by inject()

    val simulations: ObservableList<String> = FXCollections.observableArrayList<String>()
    var selected: String? by property()
    fun selectedProperty() = getProperty(SimulationsController::selected)

    var disabled: Boolean by property(true)
    fun disabledProperty() = getProperty(SimulationsController::disabled)

    fun getSimulations() {
        runAsync {
            getSimulationsInternal()
        } ui {
            simulations.addAll(it)
        }
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

    private fun getSimulationsInternal(): List<String> {
        val result = mutableListOf<String>()

        Database.driver.session().run {
            val rs = run("MATCH (s:Simulation) RETURN s")
            rs.forEach { record ->
                if (record.containsKey("s")) {
                    val node = record["s"].asNode()
                    if (node.containsKey("simId")) {
                        result.add(node["simId"].asString())
                    }
                }
            }
        }

        return result
    }
}