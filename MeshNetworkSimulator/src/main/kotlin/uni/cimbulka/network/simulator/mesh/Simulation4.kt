package uni.cimbulka.network.simulator.mesh

import javafx.geometry.Point2D
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.core.events.ShutdownEvent

class Simulation4 : BaseSimulation() {
    override fun run() {
        val nodes = mutableMapOf<Int, NetworkNode>()
        val separation = 6

        for (row in 0..4) {
            for (column in 1..5) {
                val id = (5 * row) + column
                val time = (id - 1) * 5

                val x = ((column - 1) * separation) + 1
                val y = (row * separation) + 1

                val node = getNode("Node $id", Point2D(x.toDouble(), y.toDouble()))
                nodes[id] = node
                node.insertNode(time)
            }
        }

        insert(160.0 * 1000, "SendFirstMessage") {
            val sender = nodes[1] ?: return@insert
            val target = nodes[25] ?: return@insert

            sender.controller?.let {
                val data = ApplicationData("Hello 25!")
                val packet = DataPacket.create(data, it, target.device)
                it.send(packet)
            }
        }

        insert(170.0 * 1000, "SendSecondMessage") {
            val sender = nodes[5] ?: return@insert
            val target = nodes[21] ?: return@insert

            sender.controller?.let {
                val data = ApplicationData("Hello 21!")
                val packet = DataPacket.create(data, it, target.device)
                it.send(packet)
            }
        }

        val ( first, second ) = getRandomPair(1..25)

        insert(180.0 * 1000, "SendThirdMessage- $first to $second") {
            val sender = nodes[first] ?: return@insert
            val target = nodes[second] ?: return@insert

            sender.controller?.let {
                val data = ApplicationData("Hello $second!")
                val packet = DataPacket.create(data, it, target.device)
                it.send(packet)
            }
        }

        insert(ShutdownEvent(190.0 * 1000))
        start()
    }

    private fun getRandomPair(range: IntRange): Pair<Int, Int> {
        while (true) {
            val a = range.random()
            val b = range.random()

            if (a != b) {
                return a to b
            }
        }
    }
}