package uni.cimbulka.network.simulator.gui.controllers

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Dimension2D
import tornadofx.*
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection

class  GraphController : Controller() {
    val nodes: ObservableList<PositionNode> = FXCollections.observableArrayList()
    val connections: ObservableList<Connection> = FXCollections.observableArrayList()

    val dimensionsProperty = SimpleObjectProperty<Dimension2D>(Dimension2D(25.0, 25.0))
    var dimensions: Dimension2D by dimensionsProperty
}