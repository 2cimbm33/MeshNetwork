package uni.cimbulka.network.packets

import uni.cimbulka.network.CommService
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Route
import uni.cimbulka.network.models.RouteSegment
import uni.cimbulka.network.packets.handlers.PacketHandler
import java.util.*

internal object PacketSender {
    @Suppress("UNCHECKED_CAST")
    fun <T : BasePacket> send(packet: T, session: NetworkSession) {
        packet.trace[packet.trace.size + 1] = session.localDevice
        println("Sending packet [NetworkController]: $packet")

        val handler = PacketHandler.getHandler(packet::class) as? PacketHandler<T> ?: return
        handler.send(packet, session)

        if (packet is DataPacket) {
            for (packets in session.pendingPackets.values) {
                for (pending in packets) {
                    if (pending == packet) {
                        return
                    }
                }
            }
        }

        session.processedPackets.add(packet)
    }

    internal fun getCommService(recipient: Device, session: NetworkSession): CommService? {
        return session.services.firstOrNull { recipient in it.neighbors }
    }

    internal fun discoverRoute(target: Device, session: NetworkSession) {
        val id = session.incrementPacketCount()

        for (node in session.networkGraph.borderNodes) {
            val packet = RouteDiscoveryRequest(
                    id, session.localDevice, Date().time, node, requester = session.localDevice,
                    target = target, route = mutableListOf(session.localDevice, node)
            )
            PacketSender.send(packet, session)
        }
    }
}