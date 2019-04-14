package uni.cimbulka.network.simulator.gui.views

import com.sun.javafx.geom.Vec2d
import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.gui.events.ClickedCanvas
import uni.cimbulka.network.simulator.gui.events.ClickedNode
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class GraphCanvas : Canvas() {
    private val lock = ReentrantLock()

    private var nodes = emptyList<PositionNode>()
    private var connections = emptyList<Connection>()
    private var dimensions = Dimension2D(20.0, 20.0);
    private val scaleFloor = 10.0

    private var initialDragPoint = Point2D(.0, .0)
    private var initialDragOffset = Vec2d()
    private var dragVector = Vec2d(.0, .0)
    private var drag = false
    private var imageCount = 1


    var minScale: Double by property(15.0)
        private set
    fun minScalePropery() = getProperty(GraphCanvas::minScale) as ReadOnlyProperty<Double>

    var nodeRadius: Double by property(.15)
    fun nodeRadiusProperty() = getProperty(GraphCanvas::nodeRadius)

    var fireEvents: Boolean by property(true)
    fun fireEventsProperty() = getProperty(GraphView::fireEvents)

    var zoom: Int by property(1)
        private set
    fun zoomProperty() = getProperty(GraphCanvas::zoom) as ReadOnlyProperty<Int>

    var scale: Double by property(10.0)
        private set
    fun scaleProperty() = getProperty(GraphCanvas::scale) as ReadOnlyProperty<Double>

    var offset: Vec2d by property(Vec2d(.0, .0))
        private set
    fun offsetProperty() = getProperty(GraphCanvas::offset) as ReadOnlyProperty<Vec2d>

    init {
        parentProperty().onChange {parent ->
            (parent as? Region)?.let {
                this.widthProperty().bind(it.widthProperty())
                this.heightProperty().bind(it.heightProperty())
            }
        }

        widthProperty().onChange {
            calcScale()
            redraw()
        }

        heightProperty().onChange {
            calcScale()
            redraw()
        }

        scaleProperty().onChange { value ->
            value?.let {
                redraw()
            }
        }

        offsetProperty().onChange { value ->
            value?.let {
                redraw()
            }
        }

        setOnMousePressed {
            initialDragPoint = Point2D(it.x, it.y)
            initialDragOffset.set(offset)
            dragVector = Vec2d()
            it.consume()
        }

        setOnMouseReleased {
            if (drag) {
                drag = false
                initialDragPoint = Point2D(0.0, 0.0)
                initialDragOffset = Vec2d()
                dragVector = Vec2d()
            } else if (fireEvents) {
                val hit = detectHit(Position(it.x, it.y))

                if (hit == null) {
                    FX.eventbus.fire(ClickedCanvas(Position((it.x + offset.x) / scale, (it.y + offset.y) / scale)))
                } else {
                    FX.eventbus.fire(ClickedNode(hit.id))
                }
            }
        }

        setOnDragDetected {
            this.startFullDrag()
            drag = true
        }

        setOnMouseDragged {
            dragVector.set(
                    it.x - initialDragPoint.x,
                    it.y - initialDragPoint.y
            )

            offset.x = initialDragOffset.x + initialDragPoint.x - it.x
            offset.y = initialDragOffset.y + initialDragPoint.y - it.y

            if (offset.x < 0) offset.x = .0
            else if (offset.x + width > dimensions.width * scale) offset.x = dimensions.width * scale - width

            if (offset.y < 0) offset.y = .0
            else if (offset.y + height > dimensions.height * scale) offset.y = dimensions.height * scale - height

            redraw()
        }

        setOnScroll {
            var newScale = scale + (it.deltaY / 10)
            if (newScale < minScale) newScale = minScale

            if (offset.x < 0) offset.x = .0
            else if (offset.x + width > dimensions.width * scale) offset.x = dimensions.width * scale - width

            if (offset.y < 0) offset.y = .0
            else if (offset.y + height > dimensions.height * scale) offset.y = dimensions.height * scale - height

            scale = newScale
        }
    }

    override fun isResizable() = true

    fun draw(nodes: List<PositionNode>, connections: List<Connection>, dimensions: Dimension2D) {
        lock.withLock {
            val recalculateScale = this.dimensions != dimensions
            this.nodes = nodes
            this.connections = connections
            this.dimensions = dimensions


            if (recalculateScale) calcScale()

            redraw()
        }
    }

    private fun calcScale() {
        val widthScale = width / dimensions.width
        val heightScale = height / dimensions.height

        minScale = Math.max(scaleFloor, Math.max(widthScale, heightScale))
        scale = minScale
        offset = Vec2d()
    }

    private fun redraw() {
        lock.withLock {
            graphicsContext2D.run {
                clearRect(0.0, 0.0, width, height)

                drawGrid()
                drawNodes()
                drawConnections()
            }
        }
    }

    private fun GraphicsContext.drawGrid() {
        stroke = Color.GREY
        fill = Color.GREY

        var x = 0.0
        while (x < dimensions.width * scale) {
            strokeLine(x - offset.x, .0, x - offset.x, height)
            fillText((x / scale).toInt().toString(), x - offset.x, 15.0)

            x += scale
        }

        var y = 0.0
        while (y < dimensions.height * scale) {
            strokeLine(.0, y - offset.y, width, y - offset.y)
            fillText((y / scale).toInt().toString(), 2.0, y - offset.y)

            y += scale
        }
    }

    private fun GraphicsContext.drawNodes() {
        fill = Color.BLUE
        for (node in nodes) {
            var scaledRadius = nodeRadius * scale
            if (scaledRadius < 7.5) scaledRadius = 7.5

            val ( x , y ) = node.position.map
            fillOval(x - scaledRadius, y - scaledRadius, scaledRadius * 2, scaledRadius * 2)
        }
    }

    private fun GraphicsContext.drawConnections() {
        stroke = Color.BLACK

        for (connection in connections) {
            val (first, second) = connection
            val a = nodes.firstOrNull { it.id == first } ?: continue
            val b = nodes.firstOrNull { it.id == second } ?: continue

            strokeLine(
                    a.position.map.x,
                    a.position.map.y,
                    b.position.map.x,
                    b.position.map.y
            )
        }
    }

    private val Position.map: Position
        get() {
            val calcX = ((x * scale) - offset.x)
            val calcY = ((y * scale) - offset.y)

            return Position(calcX, calcY)
        }

    private val Position.unmap: Position
        get() {
            val calcX = (x - offset.x) / scale
            val calcY = (y - offset.y) / scale

            return Position(calcX, calcY)
        }

    private val Point2D.map: Point2D
        get() {
            val calcX = ((x * scale) - offset.x)
            val calcY = ((y * scale) - offset.y)

            return Point2D(calcX, calcY)
        }

    private val Point2D.unmap: Point2D
        get() {
            val calcX = (x - offset.x) / scale
            val calcY = (y - offset.y) / scale

            return Point2D(calcX, calcY)
        }

    private fun detectHit(position: Position): PositionNode? {
        for (node in nodes) {
            if (position.distance(node.position.map) < nodeRadius) {
                return node
            }
        }

        return null
    }
}