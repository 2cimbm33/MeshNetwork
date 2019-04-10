package uni.cimbulka.network.simulator.gui.views

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.layout.Region
import javafx.util.Duration
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.GraphController

class GraphView : View("Graph view") {
    private val controller: GraphController by inject()

    val fireEventsProperty = SimpleBooleanProperty().apply { onChange { controller.fireEvents = it } }
    var fireEvents: Boolean by fireEventsProperty

    private val timeline = Timeline(KeyFrame(Duration.millis(100.0), EventHandler<ActionEvent> {
        controller.draw(canvas.graphicsContext2D)
    })).apply { cycleCount = Timeline.INDEFINITE }

    private val canvas = canvas {
        parentProperty().onChange { parent ->
            (parent as? Region)?.let {
                this.widthProperty().bind(it.widthProperty())
                this.heightProperty().bind(it.heightProperty())
            }
        }

        controller.heightProperty.bind(this.heightProperty())
        controller.widthProperty.bind(this.heightProperty())

        heightProperty().onChange {
            controller.draw(graphicsContext2D)
        }

        widthProperty().onChange {
            controller.draw(graphicsContext2D)
        }

        controller.scaleProperty().onChange {
            controller.draw(graphicsContext2D)
        }

        controller.offsetProperty().onChange {
            controller.draw(graphicsContext2D)
        }

        setOnMousePressed(controller::handleMousePressed)
        setOnMouseReleased(controller::handleMouseReleased)
        setOnMouseDragged(controller::handelMouseDragged)
        setOnScroll(controller::handleScroll)

        setOnDragDetected {
            startFullDrag()
            controller.handleDragDetected()
        }
    }

    override val root = borderpane {
        minWidth = 320.0
        minHeight = 150.0

        center = canvas
    }

    override fun onDock() {
        timeline.play()
    }

    override fun onUndock() {
        timeline.stop()
    }
}