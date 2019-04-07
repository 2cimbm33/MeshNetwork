package uni.cimbulka.network.simulator.gui.views

import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Region
import tornadofx.*
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.gui.events.ClickedCanvas
import uni.cimbulka.network.simulator.gui.events.ClickedNode
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection

class PannableCanvas : Canvas() {

    private var nodes = emptyList<PositionNode>()
    private var connections = emptyList<Connection>()

    private val visibleNodes = mutableListOf<PositionNode>()

    var maxZoom: Int by property(15)
    fun maxZoomPropery() = getProperty(PannableCanvas::maxZoom)

    var nodeRadius: Double by property(10.0)
    fun nodeRadiusProperty() = getProperty(PannableCanvas::nodeRadius)

    var fireEvents: Boolean by property(true)
    fun fireEventsProperty() = getProperty(GraphView::fireEvents)

    var zoom: Int by property(1)
        private set
    fun zoomProperty() = getProperty(PannableCanvas::zoom) as ReadOnlyProperty<Int>

    var scale: Double by property(10.0)
        private set
    fun scaleProperty() = getProperty(PannableCanvas::scale) as ReadOnlyProperty<Double>

    var offset: Point2D by property(Point2D(.0, .0))
        private set
    fun offsetProperty() = getProperty(PannableCanvas::offset) as ReadOnlyProperty<Point2D>

    init {
        parentProperty().onChange {parent ->
            (parent as? Region)?.let {
                this.widthProperty().bind(it.widthProperty())
                this.heightProperty().bind(it.heightProperty())
            }
        }

        widthProperty().onChange {
            redraw()
        }

        heightProperty().onChange {
            redraw()
        }

        scaleProperty().onChange { value ->
            value?.let {
                redraw()
            }
        }

        setOnMouseClicked {
            if (fireEvents) {
                val hit = detectHit(Position(it.x, it.y))

                if (hit == null) {
                    FX.eventbus.fire(ClickedCanvas(Position((it.x + offset.x) / scale, (it.y + offset.y) / scale)))
                } else {
                    FX.eventbus.fire(ClickedNode(hit.id))
                }
            }
        }
    }

    override fun isResizable() = true

    fun draw(nodes: List<PositionNode>, connections: List<Connection>) {
        this.nodes = nodes
        this.connections = connections

        redraw()
    }

    private fun redraw() {
        graphicsContext2D.run {
            clearRect(0.0, 0.0, width, height)

            drawNodes()
            drawConnections()
        }
    }

    private fun GraphicsContext.drawNodes() {
        visibleNodes.clear()

        for (node in nodes) {
            val ( x, y ) = node.position.actualPosition

            fillOval(x, y, nodeRadius, nodeRadius)
            visibleNodes.add(node)
        }
    }

    private fun GraphicsContext.drawConnections() {
        for (connection in connections) {
            val (first, second) = connection
            val a = nodes.firstOrNull { it.id == first } ?: continue
            val b = nodes.firstOrNull { it.id == second } ?: continue

            strokeLine(
                    a.position.actualPosition.x,
                    a.position.actualPosition.y,
                    b.position.actualPosition.x,
                    b.position.actualPosition.y
            )
        }
    }

    private val Position.actualPosition: Position
        get() {
            val calcX = ((x * scale) - offset.x) - nodeRadius / 2
            val calcY = ((y * scale) - offset.y) - nodeRadius / 2

            return Position(calcX, calcY)
        }

    private fun detectHit(position: Position): PositionNode? {
        for (node in nodes) {
            if (position.distance(node.position.actualPosition) < nodeRadius) {
                return node
            }
        }

        return null
    }
}