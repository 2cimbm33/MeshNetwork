package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.concurrent.withLock

class MoveNodeEvent(override val time: Double, args: MoveNodeEventArgs) :
        Event<MoveNodeEventArgs>("MoveNode", args) {

    override fun invoke(simulator: AbstractSimulator) {
        AdapterPool.lock.withLock {
            val (node, dx, dy, phy) = args
            //println("[$time] Moving [$id] by ${Math.abs(dx)} ${if (dx >= 0) "right" else "left"} and ${Math.abs(dy)} ${if (dy >= 0) "up" else "down"}")
            phy.moveNode(node.id, dx, dy)
            AdapterPool.updateConnections(phy)
        }
    }
}