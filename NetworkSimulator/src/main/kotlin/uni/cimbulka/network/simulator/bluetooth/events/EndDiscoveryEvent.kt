package uni.cimbulka.network.simulator.bluetooth.events

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import kotlin.concurrent.withLock

data class EndDiscoveryEventArgs(val adapter: BluetoothAdapter, @JsonIgnore val physicalLayer: PhysicalLayer) : EventArgs()

class EndDiscoveryEvent(override val time: Double, args: EndDiscoveryEventArgs) :
        Event<EndDiscoveryEventArgs>("EndDiscovery", args) {

    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val (adapter, phy) = args

            val result = mutableListOf<Node>()
            phy.keys.forEach {
                if (phy.getDistance(adapter.node.id, it) <= Constants.Bluetooth.BLUETOOTH_RANGE) {
                    phy[it]?.let { n ->
                        if (n.id != adapter.node.id)
                            result.add(n)
                    }
                }
            }
            adapter.endDiscovery(result)
        }
    }

}