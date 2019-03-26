package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.packets.PacketSender
import uni.cimbulka.network.packets.RouteDiscoveryResponse

internal class RouteDiscoveryResponseHandler : PacketHandler<RouteDiscoveryResponse> {
    override fun receive(packet: RouteDiscoveryResponse, session: NetworkSession) {
        val source = packet.source ?: return

        if (packet.recipient == session.localDevice) {
            // Am I end of the route?
            if (packet.route.first() == session.localDevice) {
                for (device in packet.route) {
                    if (device != session.localDevice) {
                        session.longDistanceVectors[device] = source
                    }
                }

                sendPendingPackets(session)
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

    private fun sendPendingPackets(session: NetworkSession) {
        for ((recipient, packets) in session.pendingPackets) {
            session.longDistanceVectors[recipient] ?: continue
            val sentPackets = mutableListOf<DataPacket>()

            for (packet in packets) {
                sentPackets.add(packet)
                packet.trace.clear()
                PacketSender.send(packet, session)
            }

            if (sentPackets.isNotEmpty()) {
                packets.removeAll(sentPackets)
            }
        }
    }

    override fun send(packet: RouteDiscoveryResponse, session: NetworkSession) {
        packet.recipient?.let {
            session.routingTable[it]?.let { next ->
                PacketSender.getCommService(next, session)?.sendPacket(packet.toString(), next)
            }
        }
    }
}