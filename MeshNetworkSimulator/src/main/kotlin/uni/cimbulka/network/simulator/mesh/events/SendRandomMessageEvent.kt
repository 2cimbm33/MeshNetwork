package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class SendRandomMessageEvent(override val time: Double, eventArgs: SendRandomMessageEventArgs) :
        Event<SendRandomMessageEventArgs>("SendRandomMessage", eventArgs) {
    override fun invoke(simulator: AbstractSimulator) {
        val (sender, recipient, size) = args

        sender.controller?.let {
            val data = ApplicationData(size.toString())
            val packet = DataPacket.create(data, it, recipient.device)
            it.send(packet)
        } ?: return
    }
}