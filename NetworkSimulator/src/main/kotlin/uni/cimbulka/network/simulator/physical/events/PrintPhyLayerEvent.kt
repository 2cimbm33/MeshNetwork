package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class PrintPhyLayerEvent(override val time: Double, args: PrintPhyLayerEventArgs) :
        Event<PrintPhyLayerEventArgs>("PrintPhyLayer",args) {
    override fun invoke(simulator: AbstractSimulator) {
        val (phy) = args
        println("[$time] Printing physical layer:")
        phy.keys.forEachIndexed { index, id ->
            println("[$index] -> ${phy[id]}")
        }
        println()
    }
}