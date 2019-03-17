package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.PacketSender
import uni.cimbulka.network.packets.RouteDiscoveryResponse

internal class RouteDiscoveryResponseHandler : PacketHandler<RouteDiscoveryResponse> {
    override fun receive(packet: RouteDiscoveryResponse, session: NetworkSession) {
        packet.route?.let {
            val index = it.getIndex(session.localDevice)

            if (it.getIndex(session.localDevice, false) == 0) {
                it.segments.first().end?.let { first ->
                    session.longDistanceVectors[first] = mutableListOf(first)

                    for (segment in it.segments) {
                        val start = segment.start ?: continue
                        val end = segment.end ?: continue

                        if (start != session.localDevice) {
                            session.longDistanceVectors[first]?.addAll(mutableListOf(start, end))
                        }
                    }
                }

                return
            }

            if (index > 0) {
                val cache = mutableListOf<Device>()
                var key: Device? = null

                for ((i, segment) in it.segments.withIndex()) {
                    val start = segment.start ?: continue
                    val end = segment.end ?: continue

                    if (i == index) {
                        cache.add(start)
                        session.longDistanceVectors[start] = cache
                        cache.clear()
                    } else if (i == index + 1) {
                        key = end
                        cache.add(end)
                    } else {
                        cache.addAll(setOf(start, end))
                    }
                }

                if (key != null)
                    session.longDistanceVectors[key] = cache

                packet.recipient = it.segments[index].start ?: return
                packet.source = session.localDevice
            }

            PacketSender.send(packet, session)
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