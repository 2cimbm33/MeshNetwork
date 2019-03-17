package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.packets.*

internal class MessageListener(private val controller: NetworkController) {
    private val session = controller.networkSession

    fun onMessageReceived(json: String) {
        println("MessageListener:onMessageReceived")

        BasePacket.fromJson(json)?.let {
            when (it) {
                is BroadcastPacket -> PacketProcessor.process(it, session)
                is DataPacket -> PacketProcessor.process(it, session)
                is HandshakeRequest -> PacketProcessor.process(it, session)
                is HandshakeResponse -> PacketProcessor.process(it, session)
                is RouteDiscoveryRequest -> PacketProcessor.process(it, session)
                is RouteDiscoveryResponse -> PacketProcessor.process(it, session)
            }

        }
        println("Done processing")
    }
}