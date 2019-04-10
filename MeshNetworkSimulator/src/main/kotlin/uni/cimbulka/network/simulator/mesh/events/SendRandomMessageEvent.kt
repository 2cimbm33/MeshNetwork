package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

class SendRandomMessageEvent(override val time: Double, args: SendRandomMessageArgs) : Event<SendRandomMessageArgs>("SendRandomMessage", args) {
    override fun invoke(simulator: AbstractSimulator) {
        val (sender, recipient) = args

        sender.controller?.let {
            val senderId = sender.id
            val recipientId = recipient.id

            it.send(DataPacket.create(ApplicationData("Message from $senderId to $recipientId"), it, recipient.device))
        } ?: return
    }
}