package uni.cimbulka.network.simulator.gui.controllers

import javafx.beans.property.ReadOnlyProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Dimension2D
import tornadofx.*
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.gui.NetworkCallbacksImpl
import uni.cimbulka.network.simulator.gui.SimulationCallbacksImpl
import uni.cimbulka.network.simulator.gui.events.ClickedCanvas
import uni.cimbulka.network.simulator.gui.events.ClickedNode
import uni.cimbulka.network.simulator.gui.events.RedrawCanvas
import uni.cimbulka.network.simulator.gui.models.PositionNode
import uni.cimbulka.network.simulator.gui.views.dialogs.SendMessageDialog
import uni.cimbulka.network.simulator.mesh.InteractiveSimulation
import java.util.logging.Logger

class InteractiveSimulationController : Controller() {
    private lateinit var simulator: InteractiveSimulation
    private lateinit var dimensions: Dimension2D

    var time: Double by property()
    fun timeProperty() = getProperty(InteractiveSimulationController::time) as ReadOnlyProperty<Double>

    var running: Boolean by property(false)
    fun runningProperty() = getProperty(InteractiveSimulationController::running) as ReadOnlyProperty<Boolean>

    var startButtonText: String by property("Start")
        private set
    fun startButtonTextProperty() = getProperty(InteractiveSimulationController::startButtonText) as ReadOnlyProperty<String>

    val events: ObservableList<EventInterface> = FXCollections.observableArrayList()

    init {
        runningProperty().onChange { value ->
            startButtonText = value?.let {
                if (it) "Stop" else "Start"
            } ?: "Start"
        }

        events.onChange {
            val positionNodes = mutableListOf<PositionNode>()
            simulator.nodes.forEach { positionNodes.add(PositionNode(it.id, it.device.name, it.position)) }

            fire(RedrawCanvas(positionNodes, simulator.connections, dimensions))
        }

        subscribe<ClickedCanvas> {
            addNode(it.position)
        }

        subscribe<ClickedNode> {
            removeNode(it.id)
        }

    }

    fun handleStartButton() {
        if (running) {
            stop()
        } else {
            start()
        }
    }

    fun addNode(position: Position) {
        if (running) {
            val callbacksImpl = NetworkCallbacksImpl(this)
            simulator.addNode(position, callbacksImpl).apply {
                callbacksImpl.deviceName = device.name
            }
        }

    }

    fun removeNode(id: String) {
        if (running) {
            simulator.nodes.firstOrNull { it.device.id.toString() == id }?.let {
                simulator.removeNode(it)
            }
        }
    }

    fun sendMessage() {
        if (running) {
            val names = mutableListOf<String>()
            simulator.nodes.forEach { names.add(it.device.name) }

            SendMessageDialog(names).showAndWait().ifPresent { result ->
                val sender = simulator.nodes.firstOrNull { it.device.name == result.sender } ?: return@ifPresent
                val recipient = simulator.nodes.firstOrNull { it.device.name == result.recipient } ?: return@ifPresent
                val data = ApplicationData(result.message)

                sender.controller?.let {
                    it.send(DataPacket.create(data, it, recipient.device))
                }
            }
        }
    }

    fun onDataReceived(data: ApplicationData, name: String) {
        Logger.getLogger(this::class.java.simpleName).info("$name received a message: $data")
    }

    private fun start() {
        if (!running) {
            dimensions = Dimension2D(25.0, 25.0)
            simulator = InteractiveSimulation(SimulationCallbacksImpl(this), Dimension2D(100.0, 100.0))
            simulator.start()
            running = true

            fire(RedrawCanvas(emptyList(), emptyList(), dimensions))
        }
    }

    private fun stop() {
        if (running && ::simulator.isInitialized) {
            simulator.stop()
            running = false
        }
    }
}