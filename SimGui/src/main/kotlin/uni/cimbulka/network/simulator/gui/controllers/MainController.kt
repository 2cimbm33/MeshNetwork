package uni.cimbulka.network.simulator.gui.controllers

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.ReadOnlyProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Dimension2D
import javafx.util.Duration
import tornadofx.*
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.gui.database.Database
import uni.cimbulka.network.simulator.gui.events.RedrawCanvas
import uni.cimbulka.network.simulator.gui.events.SwitchViewEvent
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.gui.views.RunView
import uni.cimbulka.network.simulator.gui.views.StartSimulationView
import uni.cimbulka.network.simulator.mesh.BaseSimulationCallbacks
import uni.cimbulka.network.simulator.mesh.RandomSimulation
import uni.cimbulka.network.simulator.mesh.RandomSimulationConfiguration
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot

class MainController : Controller() {

    private val times = mutableListOf<Long>()
    private lateinit var dimensions: Dimension2D

    val nodes: ObservableList<PositionNode> = FXCollections.observableArrayList()
    val connections: ObservableList<Connection> = FXCollections.observableArrayList()

    var time: Double by property(0.0)
        private set
    fun timeProperty() = getProperty(MainController::time) as ReadOnlyProperty<Double>

    fun numberOfNodesProperty() = getProperty(MainController::numberOfNodes) as ReadOnlyProperty<Int>
    var numberOfNodes: Int by property(0)
        private set

    var avgEventTime: Double by property(.0)
        private set
    fun avgEventTimeProperty() = getProperty(MainController::avgEventTime) as ReadOnlyProperty<Double>

    var numberOfEvents: Int by property(0)
        private set
    fun numberOfEventsProperty() = getProperty(MainController::numberOfEvents) as ReadOnlyProperty<Int>

    fun runSimulation(config: RandomSimulationConfiguration) {
        dimensions = config.dimensions
        val simulator = RandomSimulation(config, Database.getCollection("test"))

        simulator.simulationCallbacks = object : BaseSimulationCallbacks {
            override fun eventExecuted(snapshot: Snapshot, time: Long) {
                Platform.runLater {
                    this@MainController.time = snapshot.time
                    times.add(time)
                    numberOfEvents++

                    var total = 0L
                    times.forEach { total += it }
                    avgEventTime =  total.toDouble() / this@MainController.numberOfNodes

                    when (snapshot.eventName) {
                        "AddNode" -> {
                            val posNode = PositionNode(snapshot.nodeId, "", snapshot.position
                                    ?: Position(Double.MIN_VALUE, Double.MIN_VALUE))

                            nodes.add(posNode)
                            updateConnections(snapshot)
                        }

                        "RemoveNode" -> {
                            nodes.removeIf { it.id == snapshot.nodeId }
                            connections.removeIf { snapshot.nodeId in it }
                        }

                        "MoveNode" -> {
                            val position = snapshot.position ?: return@runLater
                            val node = nodes.firstOrNull { it.id == snapshot.nodeId } ?: return@runLater

                            node.position = position
                            updateConnections(snapshot)
                        }

                        else -> updateConnections(snapshot)
                    }
                }
            }

            override fun simulationFinished(id: String) {
                Platform.runLater {
                    timeline.stop()
                    fire(SwitchViewEvent<StartSimulationView>())
                }
            }

        }

        fire(SwitchViewEvent<RunView>())
        runAsync {
            simulator.run()
        }

        timeline.play()
    }

    private fun updateConnections(snapshot: Snapshot) {
        if (snapshot.nodeId == "null" || snapshot.connections.isEmpty()) return
        val nodeConnections = connections.filter { snapshot.nodeId in it }.toMutableList()

        if (snapshot.connections.isNotEmpty()) {
            for (id in snapshot.connections) {
                val conn = Connection(snapshot.nodeId, id)

                if (conn !in connections) {
                    connections.add(conn)
                    nodeConnections.remove(conn)
                }
            }
        }

        connections.removeAll(nodeConnections)
    }

    val timeline = Timeline(KeyFrame(Duration.millis(100.0), EventHandler<ActionEvent> {
        numberOfNodes = nodes.size
        fire(RedrawCanvas(nodes, connections, dimensions))
    })).apply { cycleCount = Timeline.INDEFINITE }
}