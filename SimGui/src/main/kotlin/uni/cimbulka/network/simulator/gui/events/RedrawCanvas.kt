package uni.cimbulka.network.simulator.gui.events

import javafx.geometry.Dimension2D
import tornadofx.*
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection

data class RedrawCanvas(val nodes: List<PositionNode>, val connections: List<Connection>, val dimension: Dimension2D) : FXEvent()