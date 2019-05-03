package uni.cimbulka.network.simulator.mesh.random

import com.sun.javafx.geom.Vec2d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.events.TimerEvent
import uni.cimbulka.network.simulator.core.events.TimerEventArgs
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.random.ticks.CreateTick
import uni.cimbulka.network.simulator.mesh.random.ticks.MoveTick
import uni.cimbulka.network.simulator.mesh.random.ticks.RandomTick
import uni.cimbulka.network.simulator.mesh.random.ticks.SendTick
import java.util.concurrent.ThreadLocalRandom
import kotlin.coroutines.EmptyCoroutineContext

class RandomTickGenerator(private val configuration: RandomTickGeneratorConfiguration) {
    var callbacks: GeneratorCallbacks? = null
    private var interval: Long = 250
    val nodes: Map<NetworkNode, Vec2d>
        get() = internalNodes.toMap()

    private val internalNodes = mutableMapOf<NetworkNode, Vec2d>()
    private val simulator = configuration.simulator
    private var mainJob: Job? = null

    private val canSend: Boolean
        get() = nodes.size > 1

    fun start() {
        if (configuration.rule.useEvents) {
            simulator.insert(TimerEvent(simulator.time, TimerEventArgs(interval.toDouble(), -1) {
                generate()
            }))
        } else {
            mainJob = CoroutineScope(EmptyCoroutineContext).launch {
                while (true) {
                    generateAsync()

                    delay(interval)
                }
            }
        }
    }

    fun stop() {
        mainJob?.let {
            if (it.isActive)
                it.cancel()
        }
    }

    fun addNode(node: NetworkNode, vector: Vec2d = getRandomVector()) {
        synchronized(node) {
            val temp = internalNodes.keys.firstOrNull { it.id == node.id }
            if (temp == null)
                internalNodes[node] = vector
        }
    }

    fun updateNode(node: NetworkNode, vector: Vec2d) {
        synchronized(node) {
            val temp = internalNodes.keys.firstOrNull { it.id == node.id } ?: return
            internalNodes.replace(temp, vector)
        }
    }

    fun removeNode(node: NetworkNode) {
        synchronized(node) {
            val duplicateNodes = mutableListOf<NetworkNode>()
            internalNodes.keys.forEach { if (it.id == node.id) duplicateNodes.add(it); }
            duplicateNodes.forEach { internalNodes.remove(it); internalNodes.keys.remove(it) }
        }
    }

    private fun generate() {
        createNode()?.let{
            callbacks?.generated(it)
        }

        for (node in nodes.keys) {
            if (callbacks != null) {
                val tick = generateForNode(node) ?: continue
                callbacks?.generated(tick)
            }
        }
    }

    private fun generateAsync() {
        synchronized(nodes) {
            createNode()?.let{
                callbacks?.generated(it)
            }

            for (node in nodes.keys) {
                if (callbacks != null) {
                    val tick = generateForNode(node) ?: continue
                    callbacks?.generated(tick)
                }
            }
        }
    }

    private fun createNode(): CreateTick? {
        val range = 0..100
        val number = range.random()

        return if (configuration.createProbability <= number) {
            genCreateTick()
        } else {
            null
        }
    }

    private fun generateForNode(node: NetworkNode): RandomTick? {
        val range = 1..100

        val type = when (range.random()) {
            in 0..25 -> if (canSend) RandomTick.Types.SEND_MESSAGE else RandomTick.Types.SEND_MESSAGE
            else -> RandomTick.Types.MOVE_NODE
        }

        return when (type) {
            RandomTick.Types.MOVE_NODE -> genMoveTick(node)
            RandomTick.Types.SEND_MESSAGE -> genSendTick(node)
            else -> genMoveTick(node)
        }
    }

    private fun genCreateTick(): CreateTick {
        synchronized(this) {
            val range = -Constants.Bluetooth.BLUETOOTH_RANGE..Constants.Bluetooth.BLUETOOTH_RANGE

            val position = if (nodes.isNotEmpty()) {
                val ref = nodes.keys.random()
                Position(ref.position.x + range.random(), ref.position.y + range.random())
            } else {
                Position(configuration.dimension.width / 2, configuration.dimension.height / 2)
            }

            val initVector = getRandomVector()

            return CreateTick(position, initVector)
        }
    }

    private fun genMoveTick(node: NetworkNode): MoveTick {
        synchronized(node) {
            val currentVector = nodes[node] ?: Vec2d()

            val newVec = if (currentVector.length < 8) {
                currentVector + getRandomVector()
            } else {
                currentVector - getRandomVector()
            }

            return MoveTick(node, newVec)
        }
    }

    private fun genSendTick(node: NetworkNode): SendTick? {
        synchronized(node) {
            val sub = getSubnetwork(node.id)
            if (sub.isEmpty()) return null

            val rec = sub.random()
            val recipient = nodes.keys.firstOrNull { it.id == rec } ?: return null

            val size = (20..200000000).random()
            return SendTick(node, recipient, size)
        }
    }

    fun getRandomVector(): Vec2d {
        val range = -1.0..1.0
        return Vec2d(range.random(), range.random())
    }

    private fun getSubnetwork(nodeId: String, result: MutableList<String> = mutableListOf()): List<String> {
        val adapter = AdapterPool.adapters[nodeId] ?: return result
        if (adapter.connections.isEmpty()) return result

        adapter.connections.forEach { id, _ ->
            if (id !in result) {
                result.add(id)

                getSubnetwork(id, result).forEach {
                    if (it !in result) {
                        result.add(it)
                    }
                }
            }
        }

        return result
    }
 }

fun ClosedFloatingPointRange<Double>.random(): Double {
    return ThreadLocalRandom.current().nextDouble(
            this.start, this.endInclusive
    )
}

val Vec2d.length: Double
    get() = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0))

operator fun Vec2d.plus(other: Vec2d): Vec2d {
    return Vec2d(
            x + other.x,
            y + other.y
    )
}

operator fun Vec2d.minus(other: Vec2d): Vec2d {
    return Vec2d(
            x - other.x,
            y - other.y
    )
}