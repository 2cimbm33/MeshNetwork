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

        if (packet is DataPacket && packet in session.pendingPackets) return

        session.processedPackets.add(packet)
    }

    internal fun getCommService(recipient: Device, session: NetworkSession): CommService? {
        return session.services.firstOrNull { recipient in it.neighbors }
    }

    internal fun discoverRoute(target: Device, session: NetworkSession) {
        discoverRoute(RouteDiscoveryRequest(
                session.incrementPacketCount(), session.localDevice, Date().time,
                requester = session.localDevice, target = target, route = Route()
        ), session)
    }

    internal fun discoverRoute(request: RouteDiscoveryRequest, session: NetworkSession) {
        for (device in session.networkGraph.borderNodes) {
            if (device == request.source) continue

            val p = request.copy().apply {
                source = session.localDevice
                recipient = device
                route?.segments?.add(RouteSegment(session.localDevice, device))
            }
            PacketSender.send(p, session)
        }
    }
}