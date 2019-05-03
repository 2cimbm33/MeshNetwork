package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.BasePacket
import uni.cimbulka.network.packets.PacketProcessor

internal class MessageListener(private val session: NetworkSession) {
    fun onMessageReceived(json: String) {
        val packet = BasePacket.fromJson(json) ?: return
        PacketProcessor.process(packet, session)
    }
}