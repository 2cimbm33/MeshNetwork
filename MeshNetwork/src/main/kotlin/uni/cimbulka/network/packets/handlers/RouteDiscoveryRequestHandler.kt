package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.PacketSender
import uni.cimbulka.network.packets.RouteDiscoveryRequest
import uni.cimbulka.network.packets.RouteDiscoveryResponse
import java.util.*

internal class RouteDiscoveryRequestHandler : PacketHandler<RouteDiscoveryRequest> {
    override fun receive(packet: RouteDiscoveryRequest, session: NetworkSession) {
        session.processedPackets.forEach {
            if (it is RouteDiscoveryRequest) {
                if (it.requester == packet.requester && it.target == packet.target) {
                    return
                }
            }
        }

        if (packet.target == session.localDevice) {
            packet.route.add(session.localDevice)

            PacketSender.send(RouteDiscoveryResponse(
                    session.incrementPacketCount(),
                    session.localDevice, Date().time,
                    packet.source, packet.route), session)

            packet.source.let {
                for (device in packet.route) {
                    if (device != session.localDevice) {
                        session.longDistanceVectors[device] = it
                    }
                }
            }
        } else if (packet.recipient == session.localDevice) {
            var dev: Device? = null
            for (it in session.routingTable.keys) {
                if (it == packet.target) {
                    dev = it
                    break
                }
            }

            if (dev == null) {
                for (node in session.networkGraph.borderNodes) {
                    if (node == packet.source) continue

                    val route = mutableListOf(*packet.route.toTypedArray(), node)
                    val newPacket = RouteDiscoveryRequest(
                            packet.id, session.localDevice, packet.timestamp, node,
                            requester = packet.requester, target = packet.target, route = route)
                    PacketSender.send(newPacket, session)
                }
                return
            } else {
                packet.recipient = dev
                packet.source = session.localDevice
                PacketSender.send(packet, session)
            }
        } else {
            PacketSender.send(packet, session)
        }
    }

    override fun send(packet: RouteDiscoveryRequest, session: NetworkSession) {
        packet.recipient.let {
            session.routingTable[it]?.let { next ->
                PacketSender.getCommService(next, session)?.sendPacket(packet.toString(), next)
            }
        }
    }
}