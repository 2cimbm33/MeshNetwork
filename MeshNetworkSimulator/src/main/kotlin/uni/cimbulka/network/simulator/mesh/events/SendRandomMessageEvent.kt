package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.random.Random

class SendRandomMessageEvent(override val time: Double, eventArgs: SendRandomMessageEventArgs) :
        Event<SendRandomMessageEventArgs>("SendRandomMessage", eventArgs) {
    override fun invoke(simulator: AbstractSimulator) {
        val (sender, recipient, size) = args

        sender.controller?.let {
            it.send(DataPacket.create(ApplicationData(genRandomByteArray(size).toString()), it, recipient.device))
        } ?: return
    }

    private fun genRandomByteArray(size: Int): String {
        var result = ""
        Random.nextBytes(size).forEach { result += it.toString() }
        return result;
    }
}