package uni.cimbulka.network.simulator.gui.views

import javafx.scene.layout.AnchorPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.GraphController

class GraphView : View("Graph view") {
    private val controller: GraphController by inject()
    private val canvas = GraphCanvas()

    var fireEvents: Boolean by property(true)
    fun fireEventsProperty() = getProperty(GraphView::fireEvents)

    override val root = anchorpane() {
        prefWidth = 920.0
        prefHeight = 680.0

        add(canvas)
        AnchorPane.setLeftAnchor(canvas, 0.0)
        AnchorPane.setTopAnchor(canvas, 0.0)
        AnchorPane.setRightAnchor(canvas, 0.0)
        AnchorPane.setBottomAnchor(canvas, 0.0)
    }

    init {
        controller.nodes.onChange {
            canvas.draw(it.list, controller.connections, controller.dimensions)
        }

        controller.connections.onChange {
            canvas.draw(controller.nodes, it.list, controller.dimensions)
        }
    }

    override fun onDock() {
        canvas.draw(emptyList(), emptyList(), controller.dimensions)
    }
}