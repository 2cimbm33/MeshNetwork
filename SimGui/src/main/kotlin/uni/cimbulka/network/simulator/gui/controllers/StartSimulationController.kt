package uni.cimbulka.network.simulator.gui.controllers

import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Dimension2D
import tornadofx.*
import uni.cimbulka.network.simulator.mesh.RandomSimulationConfiguration

class StartSimulationController : Controller() {
    private val mainController: MainController by inject()

    var createProbability: Number by property(50)
    fun createProbabilityProperty() = getProperty(StartSimulationController::createProbability)

    var numberOfPrefabs: Int by property(10)
    fun numberOfPrefabsProperty() = getProperty(StartSimulationController::numberOfPrefabs)

    var width: Double by property(50.0)
    fun widthProperty() = getProperty(StartSimulationController::width)

    var height: Double by property(50.0)
    fun heightProperty() = getProperty(StartSimulationController::height)

    var dimensions: Dimension2D by property(Dimension2D(width, height))
        private set
    fun dimensionsProperty() = getProperty(StartSimulationController::dimensions) as ReadOnlyProperty<Dimension2D>

    var time: Double by property(45 * 1000.0)
    fun timeProperty() = getProperty(StartSimulationController::time)

    init {
        widthProperty().onChange { value ->
            value?.let {
                dimensions = Dimension2D(it, height)
            }
        }

        heightProperty().onChange { value ->
            value?.let {
                dimensions = Dimension2D(width, it)
            }
        }
    }

    fun runSimulation() {
        val config = RandomSimulationConfiguration(createProbability.toInt(), numberOfPrefabs, dimensions, time)
        mainController.runSimulation(config)
    }
}