package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.simulator.Session
import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.InteractiveSimulator
import uni.cimbulka.network.simulator.core.interfaces.SimulationCallbacks
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.*

class InteractiveSimulation(callbacks: SimulationCallbacks) : InteractiveSimulator(callbacks) {
    private val phy = PhysicalLayer()

    val connections: List<Connection>
        get() {
            val result = mutableListOf<Connection>()
            for (adapter in AdapterPool.adapters.values) {
                for (id in adapter.connections.keys) {
                    val connection = Connection(adapter.node.id, id)
                    if (connection !in result) {
                        result.add(connection)
                    }
                }
            }
            return result
        }

    init {
        Session.simulator = this
    }

    fun addNode(name: String, initialPosition: Position, callbacks: NetworkCallbacks, delay: Double = 0.0): NetworkNode {
        return getNode(name, initialPosition, callbacks).apply {
            insertNode()
        }
    }

    fun removeNode(node: NetworkNode, delay: Double = 0.0) {
        node.controller?.stop()
        insert(RemoveNodeEvent(time + delay, RemoveNodeEventArgs(node, phy)))
    }

    fun moveNode(node: NetworkNode, args: MoveNodeEventArgs, delay: Double = 0.0) {
        insert(MoveNodeEvent(time + delay, args))
    }

    private fun getNode(name: String, position: Position, callbacks: NetworkCallbacks): NetworkNode {
        val controller = NetworkController(name).apply { networkCallbacks = callbacks }
        return NetworkNode(controller.localDevice, position).apply {
            this.controller = controller
        }
    }

    private fun NetworkNode.insertNode(delay: Double = 0.0) {
        val simulator = this@InteractiveSimulation
        simulator.insert(AddNodeEvent(simulator.time + delay, AddNodeEventArgs(this, phy)))
        simulator.insert(simulator.time + delay + 1, "Start${device.name}") { _ ->
            controller?.let {
                it.addCommService(BluetoothService(BluetoothAdapter(phy, this), it.localDevice.name, simulator))
                it.start()
            }
        }
    }
}