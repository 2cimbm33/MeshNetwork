package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.PacketSender

internal class BroadcastPacketHandler : PacketHandler<BroadcastPacket> {
    override fun receive(packet: BroadcastPacket, session: NetworkSession) {
        if (session.processedPackets.find { it.id == packet.id && it.source == packet.source } == null) {
            // Check for updates that have already been applied, which should be filtered and ignored
            val packetData = packet.data
            if (packetData is UpdateData) {
                session.processedPackets.filter {
                    Math.abs(it.timestamp - packet.timestamp) < 600 &&
                            it.data is UpdateData
                }.forEach {
                    val data = it.data as UpdateData
                    for (processedUpdate in data.updates) {
                        val unprocessedUpdate = packetData.updates.firstOrNull { u -> u == processedUpdate }
                        if (unprocessedUpdate != null) {
                            packetData.updates.remove(unprocessedUpdate)
                        }
                    }
                }

                if (packetData.updates.isEmpty()) return
            }
        }
    }

    override fun send(packet: BroadcastPacket, session: NetworkSession) {
        session.neighbours.values.filter {
            if (packet.trace.size == 1) true else it != packet.trace[packet.trace.size - 1]
        }.forEach {
            PacketSender.getCommService(it, session)?.sendPacket(packet.toString(), it)
        }
    }
}