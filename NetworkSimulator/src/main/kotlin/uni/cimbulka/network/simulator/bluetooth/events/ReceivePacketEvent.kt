package uni.cimbulka.network.simulator.bluetooth.events

import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.bluetooth.BluetoothPacket
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

data class ReceivePacketEventArgs(val packet: BluetoothPacket) : EventArgs()

class ReceivePacketEvent(override val time: Double, args: ReceivePacketEventArgs) :
        Event<ReceivePacketEventArgs>("ReceivePacket", args) {
    override fun invoke(simulator: AbstractSimulator) {
        val ( packet ) = args
        val adapter = AdapterPool.adapters[packet.to]

        adapter?.receivedPacket(packet)
    }
}