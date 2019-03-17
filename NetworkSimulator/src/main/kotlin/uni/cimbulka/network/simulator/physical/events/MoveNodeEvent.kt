package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class MoveNodeEvent(override val time: Double, args: MoveNodeEventArgs) :
        Event<MoveNodeEventArgs>("MoveNode", args) {

    override fun invoke(simulator: AbstractSimulator) {
        val (id, dx, dy, phy) = args
        println("[$time] Moving [$id] by ${Math.abs(dx)} ${if (dx >= 0) "right" else "left"} and ${Math.abs(dy)} ${if (dy >= 0) "up" else "down"}")
        phy.moveNode(id, dx, dy)
        val node = phy[id]
        node?.let {
            println("New node position [ ${it.position.x} ; ${it.position.y} ]\n")
        }
    }
}