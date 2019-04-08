package uni.cimbulka.network.packets

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.handlers.PacketHandler

@Suppress("UNCHECKED_CAST")
internal object PacketProcessor {
    @Synchronized
    fun <T : BasePacket> process(packet: T, session: NetworkSession) {
        val handler = PacketHandler.getHandler(packet::class) as? PacketHandler<T> ?: return
        handler.receive(packet, session)
        session.processedPackets.add(packet)
    }
}