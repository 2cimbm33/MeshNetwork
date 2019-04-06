package uni.cimbulka.network.simulator.mesh.random

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.random.ticks.*
import kotlin.coroutines.EmptyCoroutineContext

class RandomTickGenerator(val configuration: RandomTicGeneratorConfiguration) {
    private var mainJob: Job? = null
    private val nodes = mutableListOf<NetworkNode>()

    private val canCreate: Boolean
        get() = nodes.size < configuration.maxNumberOfNodes

    private val canRemove: Boolean
        get() = nodes.isNotEmpty() && configuration.maxNumberOfNodes != -1

    private val canSend: Boolean
        get() = nodes.size > 1

    fun start() {
        mainJob = CoroutineScope(EmptyCoroutineContext).launch {
            // Generate tick

            delay(20)
        }
    }

    fun stop() {
        mainJob?.let {
            if (it.isActive)
                it.cancel()
        }
    }

    private fun generate(): RandomTick? {
        val range = 0..3
        // Are we generating
        if (range.random() % 2 == 1) return null

        val type = when (range.random()) {
            0 -> if(canCreate)
                RandomTick.Types.CREATE_NODE
            else if (!canSend)
                RandomTick.Types.MOVE_NODE
            else if (range.random() % 2 == 0)
                RandomTick.Types.MOVE_NODE
            else
                RandomTick.Types.SEND_MESSAGE

            1 -> if (canSend)
                RandomTick.Types.REMOVE_NODE
            else if (!canSend)
                RandomTick.Types.MOVE_NODE
            else if (range.random() % 2 == 0)
                RandomTick.Types.MOVE_NODE
            else
                RandomTick.Types.SEND_MESSAGE

            2 -> RandomTick.Types.MOVE_NODE

            3 -> if (canSend) RandomTick.Types.SEND_MESSAGE else RandomTick.Types.SEND_MESSAGE
            else -> return null
        }

        return when (type) {
            RandomTick.Types.CREATE_NODE -> generateCreateTick()
            RandomTick.Types.REMOVE_NODE -> generateRemoveTick()
            RandomTick.Types.MOVE_NODE -> generateMoveTick()
            RandomTick.Types.SEND_MESSAGE -> generateSendTick()
        }
    }

    private fun generateCreateTick(): CreateTick {
        TODO()
    }

    private fun generateRemoveTick(): RemoveTick {
        TODO()
    }

    private fun generateMoveTick(): MoveTick {
        TODO()
    }

    private fun generateSendTick(): SendTick {
        TODO()
    }
 }