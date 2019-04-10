package uni.cimbulka.network.simulator.gui.controllers

import com.sun.javafx.geom.Vec2d
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.paint.Color
import tornadofx.*
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.gui.events.ClickedCanvas
import uni.cimbulka.network.simulator.gui.events.ClickedNode
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class  GraphController : Controller() {
    private val lock = ReentrantLock()
    private val scaleFloor = 10.0

    private var initialDragPoint = Point2D(.0, .0)
    private var initialDragOffset = Vec2d()
    private var dragVector = Vec2d(.0, .0)
    private var drag = false

    val nodes: ObservableList<PositionNode> = FXCollections.observableArrayList()
    val connections: ObservableList<Connection> = FXCollections.observableArrayList()

    val fireEventsProperty = SimpleBooleanProperty()
    var fireEvents: Boolean by fireEventsProperty

    val dimensionsProperty = SimpleObjectProperty<Dimension2D>(Dimension2D(25.0, 25.0))
    var dimensions: Dimension2D by dimensionsProperty

    val widthProperty = SimpleDoubleProperty()
    val width: Double by widthProperty

    val heightProperty = SimpleDoubleProperty()
    val height: Double by heightProperty

    var minScale: Double by property(15.0)
        private set
    fun minScalePropery() = getProperty(GraphController::minScale) as ReadOnlyProperty<Double>

    var nodeRadius: Double by property(.15)
    fun nodeRadiusProperty() = getProperty(GraphController::nodeRadius)

    var zoom: Int by property(1)
        private set
    fun zoomProperty() = getProperty(GraphController::zoom) as ReadOnlyProperty<Int>

    var scale: Double by property(10.0)
        private set
    fun scaleProperty() = getProperty(GraphController::scale) as ReadOnlyProperty<Double>

    var offset: Vec2d by property(Vec2d(.0, .0))
        private set
    fun offsetProperty() = getProperty(GraphController::offset) as ReadOnlyProperty<Vec2d>

    fun handleMousePressed(event: MouseEvent) {
        initialDragPoint = Point2D(event.x, event.y)
        initialDragOffset.set(offset)
        dragVector = Vec2d()
        event.consume()
    }

    fun handleMouseReleased(event: MouseEvent) {
        if (drag) {
            drag = false
            initialDragPoint = Point2D(0.0, 0.0)
            initialDragOffset = Vec2d()
            dragVector = Vec2d()
        } else if (fireEvents) {
            val hit = detectHit(Position(event.x, event.y))

            if (hit == null) {
                FX.eventbus.fire(ClickedCanvas(Position((event.x + offset.x) / scale, (event.y + offset.y) / scale)))
            } else {
                FX.eventbus.fire(ClickedNode(hit.id))
            }
        }
    }

    fun handleDragDetected() {
        drag = true
    }

    fun handelMouseDragged(event: MouseEvent) {
        dragVector.set(
                event.x - initialDragPoint.x,
                event.y - initialDragPoint.y
        )

        val newOffset = Vec2d(
                initialDragOffset.x + initialDragPoint.x - event.x,
                initialDragOffset.y + initialDragPoint.y - event.y
        )

        offset = verifyOffset(newOffset)
    }

    fun handleScroll(event: ScrollEvent) {
        var newScale = scale + (event.deltaY / 10)
        if (newScale < minScale) newScale = minScale

        offset = verifyOffset(offset)

        scale = newScale
    }

    fun draw(context: GraphicsContext) {
        lock.withLock {
            val recalculateScale = this.dimensions != dimensions
            if (recalculateScale) calcScale()

            redraw(context)
        }
    }

    fun calcScale() {
        val widthScale = width / dimensions.width
        val heightScale = height / dimensions.height

        minScale = Math.max(scaleFloor, Math.max(widthScale, heightScale))
        scale = if (scale < minScale) minScale else scale


        offset = Vec2d()
    }

    private fun redraw(context: GraphicsContext) {
        lock.withLock {
            context.run {
                clearRect(0.0, 0.0, width, height)

                drawGrid()
                drawNodes()
                drawConnections()
            }
        }
    }

    private fun verifyOffset(offset: Vec2d): Vec2d {
        if (offset.x < 0) offset.x = .0
        else if (offset.x + width > dimensions.width * scale) offset.x = dimensions.width * scale - width

        if (offset.y < 0) offset.y = .0
        else if (offset.y + height > dimensions.height * scale) offset.y = dimensions.height * scale - height

        return offset
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