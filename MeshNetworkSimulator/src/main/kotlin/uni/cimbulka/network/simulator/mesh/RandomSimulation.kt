package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Dimension2D
import org.litote.kmongo.coroutine.CoroutineCollection
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.core.events.ShutdownEvent
import uni.cimbulka.network.simulator.core.events.TimerEvent
import uni.cimbulka.network.simulator.core.events.TimerEventArgs
import uni.cimbulka.network.simulator.mesh.events.StartNodeEvent
import uni.cimbulka.network.simulator.mesh.events.StartNodeEventArgs
import uni.cimbulka.network.simulator.mesh.random.GeneratorCallbacks
import uni.cimbulka.network.simulator.mesh.random.RandomTickGenerator
import uni.cimbulka.network.simulator.mesh.random.RandomTickGeneratorConfiguration
import uni.cimbulka.network.simulator.mesh.random.ticks.*
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.*
import java.util.concurrent.ThreadLocalRandom

data class RandomSimulationConfiguration(val createProbability: Int,
                                         val preCreatedNumberOfNodes: Int,
                                         val dimensions: Dimension2D,
                                         val duration: Double)

class RandomSimulation(private val config: RandomSimulationConfiguration, collection: CoroutineCollection<Snapshot>) : BaseSimulation(collection) {
    private val generator = RandomTickGenerator(RandomTickGeneratorConfiguration(
            this, RandomTickGeneratorConfiguration.Rule.EVENT_DRIVEN, config.createProbability, config.dimensions
    ))

    init {
        monitor.physicalLayer = PhysicalLayer(config.dimensions.width, config.dimensions.height)

        generator.callbacks = object : GeneratorCallbacks {
            override fun generated(tick: RandomTick) {
                when (tick) {
                    is CreateTick -> {
                        getNode("Node ${generator.nodes.size + 1}", tick.initPosition).apply {
                            insertNode(time)
                        }
                    }

                    is MoveTick -> {
                        generator.updateNode(tick.node, tick.vector)
                    }

                    is SendTick -> {
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

    override fun run() {
        for (i in 1..config.preCreatedNumberOfNodes) {
            val ref = if (generator.nodes.isEmpty()) {
                Position(config.dimensions.width / 2, config.dimensions.height / 2)
            } else {
                generator.nodes.keys.random().position
            }

            val startingPosition = Position(
                    ref.x + ThreadLocalRandom.current().nextDouble(-8.0, 8.0),
                    ref.y + ThreadLocalRandom.current().nextDouble(-8.0, 8.0)
            )

            val node = getNode("Node $i", startingPosition).apply {
                insertNode(time)
            }
            generator.addNode(node)
        }

        insert(TimerEvent(0.0, TimerEventArgs(100.0,-1) {
            for ((node, vector) in generator.nodes) {
                val (x, y) = node.position

                val newX = x + (vector.x / 10)
                val newY = y + (vector.y / 10)

                var remove = false

                if (newX < 0.0 || newX >= config.dimensions.width) remove = true
                if (newY + vector.y == .0 || newY + vector.y >= config.dimensions.height) remove = true

                if (remove) {
                    insert(RemoveNodeEvent(time, RemoveNodeEventArgs(node, phy)))
                    generator.removeNode(node)
                } else {
                    insert(MoveNodeEvent(time, MoveNodeEventArgs(
                            node, vector.x / 10, vector.y / 10, phy
                    )))
                }
            }
        }))

        insert(ShutdownEvent(config.duration))
        start()
    }

    override fun start() {
        generator.start()

        super.start()
    }

    override fun stop() {
        generator.stop()

        super.stop()
    }

    private fun NetworkNode.insertNode(time: Double) {
        val simulator = this@RandomSimulation
        simulator.insert(AddNodeEvent(time, AddNodeEventArgs(this, phy)))
        simulator.insert(StartNodeEvent(time + 1, StartNodeEventArgs(this, phy)))
        insert(0.0, "AddNodeToGenerator") {
            generator.addNode(this)
        }
    }
}