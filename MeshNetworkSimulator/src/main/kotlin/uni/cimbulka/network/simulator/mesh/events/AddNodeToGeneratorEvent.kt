package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class AddNodeToGeneratorEvent(override val time: Double, args: AddNodeToGeneratorEventArgs) :
        Event<AddNodeToGeneratorEventArgs>("AddNodeToGenerator", args) {
    override fun invoke(simulator: AbstractSimulator) {
        val (node, generator) = args
        generator.addNode(node)
    }
}