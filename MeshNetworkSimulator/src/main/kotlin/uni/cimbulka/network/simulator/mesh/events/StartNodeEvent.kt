package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.mesh.BluetoothService
import kotlin.concurrent.withLock

class StartNodeEvent(override val time: Double, args: StartNodeEventArgs) : Event<StartNodeEventArgs>("StartNode", args) {
    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val ( node, phy ) = args

            node.controller?.let {
                it.addCommService(BluetoothService(BluetoothAdapter(phy, node), it.localDevice.name, simulator))
                it.start()
            }
        }
    }

}