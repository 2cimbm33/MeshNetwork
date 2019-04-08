package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.concurrent.withLock

class AddNodeEvent(override val time: Double, args: AddNodeEventArgs) :
        Event<AddNodeEventArgs>("AddNode", args) {

    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val (node, phy) = args
            phy.addNode(node)
        }
    }
}