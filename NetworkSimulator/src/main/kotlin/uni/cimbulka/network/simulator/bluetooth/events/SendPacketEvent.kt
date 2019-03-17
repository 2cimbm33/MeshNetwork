package uni.cimbulka.network.simulator.bluetooth.events

import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.bluetooth.BluetoothPacket
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.random.Random

data class SendPacketEventArgs(val packet: BluetoothPacket) : EventArgs()

class SendPacketEvent(override val time: Double, args: SendPacketEventArgs) :
        Event<SendPacketEventArgs>("SendPacket", args) {

    override fun invoke(simulator: AbstractSimulator) {
        val ( packet ) = args

        val delay = Random.nextDouble(Constants.Bluetooth.TRANSPORT_DELAY_RANGE.start, Constants.Bluetooth.TRANSPORT_DELAY_RANGE.endInclusive)
        simulator.insert(ReceivePacketEvent(time + delay,
                ReceivePacketEventArgs(packet)))
    }

}