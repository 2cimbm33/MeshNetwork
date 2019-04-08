package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.concurrent.withLock

class RemoveNodeEvent(override val time: Double, args: RemoveNodeEventArgs) : Event<RemoveNodeEventArgs>("RemoveNode", args) {
    @Synchronized
    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val (node, phy) = args

            phy.removeNode(node.id)
            AdapterPool.adapters[node.id]
            AdapterPool.updateConnections(phy)
        }
    }
}