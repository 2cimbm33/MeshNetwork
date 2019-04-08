package uni.cimbulka.network.simulator.gui.views

import javafx.scene.layout.AnchorPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.events.RedrawCanvas

class GraphView : View("Graph view") {
    private val canvas = PannableCanvas()

    var fireEvents: Boolean by property(true)
    fun fireEventsProperty() = getProperty(GraphView::fireEvents)

    override val root = anchorpane() {
        add(canvas)
        AnchorPane.setLeftAnchor(canvas, 0.0)
        AnchorPane.setTopAnchor(canvas, 0.0)
        AnchorPane.setRightAnchor(canvas, 0.0)
        AnchorPane.setBottomAnchor(canvas, 0.0)
    }

    init {
        canvas.fireEventsProperty().bind(fireEventsProperty())

        subscribe<RedrawCanvas> { event ->
            canvas.draw(event.nodes, event.connections, event.dimension)
        }
    }
}