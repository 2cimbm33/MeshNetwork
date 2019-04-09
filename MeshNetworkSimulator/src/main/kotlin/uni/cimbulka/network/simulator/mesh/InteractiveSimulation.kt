package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Dimension2D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.Session
import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.ContinuousSimulator
import uni.cimbulka.network.simulator.core.interfaces.SimulationCallbacks
import uni.cimbulka.network.simulator.mesh.random.GeneratorCallbacks
import uni.cimbulka.network.simulator.mesh.random.RandomTickGenerator
import uni.cimbulka.network.simulator.mesh.random.RandomTickGeneratorConfiguration
import uni.cimbulka.network.simulator.mesh.random.ticks.MoveTick
import uni.cimbulka.network.simulator.mesh.random.ticks.RandomTick
import uni.cimbulka.network.simulator.mesh.random.ticks.SendTick
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.*
import kotlin.coroutines.EmptyCoroutineContext

class InteractiveSimulation(callbacks: SimulationCallbacks, val dimensions: Dimension2D) : ContinuousSimulator(callbacks) {

    private var moveJob: Job? = null
    private val phy = PhysicalLayer(dimensions.width, dimensions.height)
    private val generator = RandomTickGenerator(RandomTickGeneratorConfiguration(
            this,
            RandomTickGeneratorConfiguration.Rule.CONTINUOUS,
            -1,
            dimensions)
    )

    val nodes: List<NetworkNode>
        get() = generator.nodes.keys.toList()

    val connections: List<Connection>
        get() {
            AdapterPool.lock.lock()
            try {
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
            } catch (e: Exception) {
                throw e
            } finally {
                AdapterPool.lock.unlock()
            }
        }

    init {
        Session.simulator = this

        generator.callbacks = object : GeneratorCallbacks {
            override fun generated(tick: RandomTick) {
                when (tick) {
                    is MoveTick -> {
                        synchronized(tick) {
                            generator.updateNode(tick.node, tick.vector)
                        }
                    }

                    is SendTick -> {
                        synchronized(tick) {
                            tick.sender.controller?.let {
                                val senderId = tick.sender.id
                                val recipientId = tick.recipient.id

                                it.send(DataPacket.create(ApplicationData("Message from $senderId to $recipientId"), it, tick.recipient.device))
                            } ?: return
                        }
                    }
                }
            }
        }
    }

    fun addNode(initialPosition: Position, callbacks: NetworkCallbacks): NetworkNode {
        synchronized(this) {
            val name = "Node ${generator.nodes.size + 1}"
            val node = getNode(name, initialPosition, callbacks).apply {
                insertNode()
            }

            generator.addNode(node)
            return node
        }
    }

    fun removeNode(node: NetworkNode, delay: Double = 0.0) {
        node.controller?.stop()
        insert(RemoveNodeEvent(time + delay, RemoveNodeEventArgs(node, phy)))
        generator.removeNode(node)
    }

    override fun start() {
        generator.start()
        startMoveNodes()
        super.start()
    }

    override fun stop() {
        generator.stop()
        stopMoveNodes()
        super.stop()
    }

    private fun startMoveNodes() {
        moveJob = CoroutineScope(EmptyCoroutineContext).launch {
            while (true) {
                for ((node, vector) in generator.nodes) {
                    val (x, y) = node.position

                    val newX = x + (vector.x / 10)
                    val newY = y + (vector.y / 10)

                    if (newX < 0.0 || newX >= dimensions.width) vector.x *= -1
                    if (newY + vector.y == .0 || newY + vector.y >= dimensions.height) vector.y *= -1

                    insert(MoveNodeEvent(time, MoveNodeEventArgs(
                            node, vector.x / 10, vector.y / 10, phy
                    )))
                }

                delay(100)
            }
        }
    }

    private fun stopMoveNodes() {
        moveJob?.cancel()
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