package uni.cimbulka.network.packets

import uni.cimbulka.network.CommService
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.handlers.PacketHandler

internal object PacketSender {
    inline fun <reified T : BasePacket> send(packet: T, session: NetworkSession) {
        println("Sending packet [NetworkController]: $packet")
        packet.trace[packet.trace.size + 1] = session.localDevice

        val handler = PacketHandler.getHandler<T>()
        handler?.send(packet, session)

        session.processedPackets.add(packet)
    }

    internal fun getCommService(recipient: Device, session: NetworkSession): CommService? {
        return session.services.firstOrNull { recipient in it.neighbors }
    }
}