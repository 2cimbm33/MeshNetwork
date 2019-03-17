package uni.cimbulka.network.simulator.bluetooth.events

import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

data class EndDiscoveryEventArgs(val adapter: BluetoothAdapter, val neighbors: List<Node>) : EventArgs()

class EndDiscoveryEvent(override val time: Double, args: EndDiscoveryEventArgs) :
        Event<EndDiscoveryEventArgs>("EndDiscovery", args) {

    override fun invoke(simulator: AbstractSimulator) {
        val (adapter, neighbors) = args
        adapter.endDiscovery(neighbors)
    }

}