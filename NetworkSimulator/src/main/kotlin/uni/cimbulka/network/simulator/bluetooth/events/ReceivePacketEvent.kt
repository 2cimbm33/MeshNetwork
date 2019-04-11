package uni.cimbulka.network.simulator.bluetooth.events

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.bluetooth.BluetoothPacket
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.concurrent.withLock

data class ReceivePacketEventArgs(val packet: BluetoothPacket, val adapter: BluetoothAdapter, @JsonIgnore val senderAdapter: BluetoothAdapter) : EventArgs()

class ReceivePacketEvent(override val time: Double, args: ReceivePacketEventArgs) :
        Event<ReceivePacketEventArgs>("ReceivePacket", args) {
    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val ( packet, adapter, sender ) = args

            if (adapter.hasConnection(packet.from)) {
                adapter.receivedPacket(packet)
            }

        }
    }
}