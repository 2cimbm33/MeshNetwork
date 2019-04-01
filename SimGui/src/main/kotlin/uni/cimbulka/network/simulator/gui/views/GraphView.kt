package uni.cimbulka.network.simulator.gui.views

import tornadofx.View
import tornadofx.doubleBinding
import tornadofx.pane
import uni.cimbulka.network.simulator.gui.controllers.GraphController

class GraphView : View("Graph view") {
    private val controller: GraphController by inject()

    override val root = pane {
        heightProperty().doubleBinding(controller.heightProperty) { it?.toDouble() ?: Double.NaN }
        widthProperty().doubleBinding(controller.widthProperty) { it?.toDouble() ?: Double.NaN }

        add(controller.group)
    }
}