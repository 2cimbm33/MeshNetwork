package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.PacketSender
import uni.cimbulka.network.packets.RouteDiscoveryResponse

internal class RouteDiscoveryResponseHandler : PacketHandler<RouteDiscoveryResponse> {
    override fun receive(packet: RouteDiscoveryResponse, session: NetworkSession) {
        val source = packet.source

        if (packet.recipient == session.localDevice) {
            // Am I end of the route?
            if (packet.route.first() == session.localDevice) {
                for (device in packet.route) {
                    if (device != session.localDevice) {
                        session.longDistanceVectors[device] = source
                    }
                }

                PacketSender.sendPendingPackets(session)
            } else {
                val currentIndex = packet.route.indexOf(session.localDevice)
                if (currentIndex == -1) return

                val nextRecipient = packet.route[currentIndex - 1]

                for ((index, device) in packet.route.withIndex()) {

                    when {
                        index > currentIndex -> {
                            session.longDistanceVectors[device] = source
                        }
                        else -> {
                            if (device != session.localDevice)
                                session.longDistanceVectors[device] = nextRecipient
                        }
                    }
                }

                packet.source = session.localDevice
                packet.recipient = nextRecipient
                PacketSender.send(packet, session)
            }
        } else {
            PacketSender.send(packet, session)
        }
    }

    override fun send(packet: RouteDiscoveryResponse, session: NetworkSession) {
        session.routingTable[packet.recipient]?.let { next ->
            PacketSender.getCommService(next, session)?.sendPacket(packet.toString(), next)
        }
    }
}