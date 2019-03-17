package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class AddNodeEvent(override val time: Double, args: AddNodeEventArgs) :
        Event<AddNodeEventArgs>("AddNode", args) {

    override fun invoke(simulator: AbstractSimulator) {
        val (node, phy) = args
        phy.addNode(node)
    }
}