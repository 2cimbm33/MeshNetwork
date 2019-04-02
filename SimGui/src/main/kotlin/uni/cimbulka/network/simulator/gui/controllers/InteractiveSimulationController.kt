package uni.cimbulka.network.simulator.gui.controllers

import javafx.beans.property.ReadOnlyProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Alert
import tornadofx.*
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.gui.NetworkCallbacksImpl
import uni.cimbulka.network.simulator.gui.SimulationCallbacksImpl
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.gui.views.dialogs.AddNodeDialog
import uni.cimbulka.network.simulator.gui.views.dialogs.RemoveNodeDialog
import uni.cimbulka.network.simulator.gui.views.dialogs.SendMessageDialog
import uni.cimbulka.network.simulator.mesh.InteractiveSimulation
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection

class InteractiveSimulationController : Controller() {
    private lateinit var simulator: InteractiveSimulation
    private val graphController: GraphController by inject()

    var time: Double by property()
    fun timeProperty() = getProperty(InteractiveSimulationController::time) as ReadOnlyProperty<Double>

    var running: Boolean by property(false)
    fun runningProperty() = getProperty(InteractiveSimulationController::running) as ReadOnlyProperty<Boolean>

    var startButtonText: String by property("Start")
        private set
    fun startButtonTextProperty() = getProperty(InteractiveSimulationController::startButtonText) as ReadOnlyProperty<String>

    val nodes: ObservableList<NetworkNode> = FXCollections.observableArrayList()
    val connections: ObservableList<Connection> = FXCollections.observableArrayList()
    val events: ObservableList<EventInterface> = FXCollections.observableArrayList()

    init {
        runningProperty().onChange { value ->
            startButtonText = value?.let {
                if (it) "Stop" else "Start"
            } ?: "Start"
        }

        events.onChange {
            connections.clear()
            connections.addAll(simulator.connections)

            val positionNodes = mutableListOf<PositionNode>()
            nodes.forEach { positionNodes.add(PositionNode(it.id, it.device.name, it.position)) }

            println(connections)
            println(positionNodes)

            graphController.draw(positionNodes, connections)
        }
    }

    fun handleStartButton() {
        if (running) {
            stop()
        } else {
            start()
        }
    }

    fun addNode() {
        if (running) {
            AddNodeDialog().showAndWait().ifPresent {
                val node = simulator.addNode(it.name, it.position, NetworkCallbacksImpl(it.name, this), it.delay)
                nodes.add(node)
            }
        }

    }

    fun removeNode() {
        if (running) {
            val names = mutableListOf<String>()
            nodes.forEach { names.add(it.device.name) }

            RemoveNodeDialog(names).showAndWait().ifPresent {
                removeNode(it.name)
            }
        }
    }

    fun removeNode(name: String) {
        if (running) {
            nodes.firstOrNull { it.device.name == name }?.let {
                simulator.removeNode(it)
            }
        }
    }

    fun moveNode() {
        // TODO: MoveNodeDialog open modal
    }

    fun sendMessage() {
        if (running) {
            val names = mutableListOf<String>()
            nodes.forEach { names.add(it.device.name) }

            SendMessageDialog(names).showAndWait().ifPresent { result ->
                val sender = nodes.firstOrNull { it.device.name == result.sender } ?: return@ifPresent
                val recipient = nodes.firstOrNull { it.device.name == result.recipient } ?: return@ifPresent
                val data = ApplicationData(result.message)

                sender.controller?.let {
                    it.send(DataPacket.create(data, it, recipient.device))
                }
            }
        }
    }

    fun onDataReceived(data: ApplicationData, name: String) {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = "Message received"
            headerText = "$name received a message"
            contentText = data.toString()
        }.show()
    }

    private fun start() {
        if (!running) {
            simulator = InteractiveSimulation(SimulationCallbacksImpl(this))
            simulator.start()
            running = true
        }
    }

    private fun stop() {
        if (running && ::simulator.isInitialized) {
            simulator.stop()
            running = false
        }
    }
}