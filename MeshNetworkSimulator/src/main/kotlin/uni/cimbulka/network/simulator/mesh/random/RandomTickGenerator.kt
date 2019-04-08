package uni.cimbulka.network.simulator.mesh.random

import com.sun.javafx.geom.Vec2d
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.events.TimerEvent
import uni.cimbulka.network.simulator.core.events.TimerEventArgs
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.random.ticks.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.coroutines.EmptyCoroutineContext

class RandomTickGenerator(val configuration: RandomTickGeneratorConfiguration) {
    var callbacks: GeneratorCallbacks? = null
    var interval: Long = 1 * 1000
    val nodes: Map<NetworkNode, Vec2d>
        get() = internalNodes.toMap()

    private val internalNodes = mutableMapOf<NetworkNode, Vec2d>()
    private val simulator = configuration.simulator
    private var mainJob: Job? = null

    private val canCreate: Boolean
        get() = nodes.size < configuration.maxNumberOfNodes

    private val canRemove: Boolean
        get() = nodes.isNotEmpty() && configuration.maxNumberOfNodes != -1

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

    fun addNode(node: NetworkNode) {
        synchronized(node) {
            val temp = internalNodes.keys.firstOrNull { it.id == node.id }
            if (temp == null)
                internalNodes[node] = getRandomVector()
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
            duplicateNodes.forEach { internalNodes.remove(it) }
        }
    }

    private fun generate() {
        for (node in nodes.keys) {
            if (callbacks != null) {
                val tick = generateForNode(node) ?: continue
                callbacks?.generated(tick)
            }
        }
    }

    private fun generateAsync() {
        synchronized(nodes) {
            for (node in nodes.keys) {
                if (callbacks != null) {
                    CoroutineScope(EmptyCoroutineContext).launch {
                        val tick = generateForNode(node) ?: return@launch
                        callbacks?.generated(tick)
                    }
                }
            }
        }
    }

    private fun generateForNode(node: NetworkNode): RandomTick? {
        val range = 0..3
        if (range.random() != 0) return null

        val type = when (range.random()) {
            0 -> if(canCreate)
                RandomTick.Types.CREATE_NODE
            else if (!canSend)
                RandomTick.Types.MOVE_NODE
            else if (range.random() != 0)
                RandomTick.Types.MOVE_NODE
            else
                RandomTick.Types.SEND_MESSAGE

            1 -> if (canRemove)
                RandomTick.Types.REMOVE_NODE
            else if (!canSend)
                RandomTick.Types.MOVE_NODE
            else if (range.random() != 0)
                RandomTick.Types.MOVE_NODE
            else
                RandomTick.Types.SEND_MESSAGE

            2 -> RandomTick.Types.MOVE_NODE

            3 -> if (canSend) RandomTick.Types.SEND_MESSAGE else RandomTick.Types.SEND_MESSAGE
            else -> return null
        }

        return when (type) {
            RandomTick.Types.CREATE_NODE -> genCreateTick(node)
            RandomTick.Types.REMOVE_NODE -> genRemoveTick(node)
            RandomTick.Types.MOVE_NODE -> genMoveTick(node)
            RandomTick.Types.SEND_MESSAGE -> genSendTick(node)
        }
    }

    private fun genCreateTick(node: NetworkNode): CreateTick {
        synchronized(node) {
            val range = -Constants.Bluetooth.BLUETOOTH_RANGE..Constants.Bluetooth.BLUETOOTH_RANGE

            val position = if (nodes.isNotEmpty()) {
                val ref = nodes.keys.random()
                Position(ref.position.x + range.random(), ref.position.y + range.random())
            } else {
                Position(configuration.dimension.width / 2, configuration.dimension.height / 2)
            }

            val initVector = getRandomVector()

            return CreateTick(node, position, initVector)
        }
    }

    private fun genRemoveTick(node: NetworkNode): RemoveTick {
        return RemoveTick(node)
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
            lateinit var recipient: NetworkNode

            do {
                try {
                    recipient = nodes.keys.random()
                } catch (e: Exception) {
                    return null
                }
            } while (recipient == node)

            return SendTick(node, recipient)
        }
    }

    fun getRandomVector(): Vec2d {
        val range = -1.0..1.0
        return Vec2d(range.random(), range.random())
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