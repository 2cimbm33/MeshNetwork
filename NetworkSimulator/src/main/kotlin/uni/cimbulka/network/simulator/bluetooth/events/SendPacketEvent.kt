package uni.cimbulka.network.simulator.bluetooth.events

import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.bluetooth.BluetoothAdapter
import uni.cimbulka.network.simulator.bluetooth.BluetoothPacket
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.concurrent.withLock

data class SendPacketEventArgs(val packet: BluetoothPacket, val adapter: BluetoothAdapter) : EventArgs()

class SendPacketEvent(override val time: Double, args: SendPacketEventArgs) :
        Event<SendPacketEventArgs>("SendPacket", args) {

    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val (packet, adapter) = args
            val recAdapter = AdapterPool.adapters[packet.to] ?: return

            if (!adapter.hasConnection(packet.to)) {
                adapter.createConnection(recAdapter)
            }

            val size = packet.data.toByteArray().size
            val delay = size / Constants.Bluetooth.TRANSMISSION_RATE

            simulator.insert(ReceivePacketEvent(time + delay,
                    ReceivePacketEventArgs(packet, recAdapter, adapter)))
        }
    }
}