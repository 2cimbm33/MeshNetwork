package uni.cimbulka.network.simulator.gui.controllers

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.*

class RunController : Controller() {
    private val mainController: MainController by inject()

    val timeProperty = SimpleDoubleProperty().apply { bind(mainController.timeProperty()) }
    val numberOfNodesProperty = SimpleIntegerProperty().apply { bind(mainController.numberOfNodesProperty) }
    val eventTimeProperty = SimpleDoubleProperty().apply { bind(mainController.avgEventTimeProperty()) }
    val numberOfEventProperty = SimpleIntegerProperty().apply { bind(mainController.numberOfEventsProperty()) }
}
