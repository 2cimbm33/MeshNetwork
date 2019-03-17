package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.RouteSegment
import uni.cimbulka.network.packets.PacketSender
import uni.cimbulka.network.packets.RouteDiscoveryRequest
import uni.cimbulka.network.packets.RouteDiscoveryResponse
import java.util.*

internal class RouteDiscoveryRequestHandler : PacketHandler<RouteDiscoveryRequest> {
    override fun receive(packet: RouteDiscoveryRequest, session: NetworkSession) {
        // Have I already processed this packet?
        session.processedPackets.forEach {
            if (it is RouteDiscoveryRequest) {
                if (it.requester == packet.requester && it.target == packet.target) {
                    return
                }
            }
        }

        // Am I the rebroadcast packet?
        if (packet.recipient == session.localDevice) {
            // Then check nodes in zone and either report back or rebroadcast the packet
            var dev: Device? = null
            for (it in session.networkGraph.devices) {
                if (it == packet.target) {
                    dev = it
                    break
                }
            }

            if (dev == null) {
                // Add yourself to the route
                packet.route?.segments?.add(RouteSegment(packet.source, session.localDevice))
                // Tell network controller to discover route
                session.controller.discoverRoute(packet)
            } else {
                val route = packet.route ?: return
                route.segments.add(RouteSegment(packet.source, session.localDevice))
                route.segments.add(RouteSegment(session.localDevice, dev))

                PacketSender.send(RouteDiscoveryResponse(
                        session.incrementPacketCount(),
                        session.localDevice, Date().time,
                        packet.source, route), session)

                packet.source?.let {
                    session.longDistanceVectors[it] = mutableSetOf(it)

                    for (segment in route.segments) {
                        val start = segment.start ?: continue
                        val end = segment.end ?: continue

                        if (end == it ) {
                            session.longDistanceVectors[it]?.add(start)
                        } else if (start == it) {
                            break
                        } else {
                            session.longDistanceVectors[it]?.addAll(mutableListOf(start, end))
                        }
                    }
                }

            }
        } else {
            // Send it towards the recipient
            PacketSender.send(packet, session)
        }
    }

    override fun send(packet: RouteDiscoveryRequest, session: NetworkSession) {
        packet.recipient?.let {
            session.routingTable[it]?.let { next ->
                PacketSender.getCommService(next, session)?.sendPacket(packet.toString(), next)
            }
        }
    }
}