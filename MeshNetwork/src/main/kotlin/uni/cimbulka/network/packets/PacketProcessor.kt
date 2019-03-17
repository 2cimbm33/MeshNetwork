package uni.cimbulka.network.packets

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.handlers.PacketHandler

internal object PacketProcessor {
    inline fun <reified T : BasePacket> process(packet: T, session: NetworkSession) {
        val handler = PacketHandler.getHandler<T>()
        handler?.receive(packet, session)
    }
}