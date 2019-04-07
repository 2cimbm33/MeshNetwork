package uni.cimbulka.network.simulator.gui.views

import tornadofx.*
import uni.cimbulka.network.simulator.gui.events.RedrawCanvas

class GraphView : View("Graph view") {
    private val canvas = PannableCanvas()

    var fireEvents: Boolean by property(true)
    fun fireEventsProperty() = getProperty(GraphView::fireEvents)

    override val root = pane {
        add(canvas)
    }

    init {
        canvas.fireEventsProperty().bind(fireEventsProperty())

        subscribe<RedrawCanvas> { event ->
            canvas.draw(event.nodes, event.connections)
        }
    }
}