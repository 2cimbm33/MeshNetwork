package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.PacketSender

internal class BroadcastPacketHandler : PacketHandler<BroadcastPacket> {
    override fun receive(packet: BroadcastPacket, session: NetworkSession) {
        if (session.processedPackets.find { it.id == packet.id && it.source == packet.source } == null) {
            // Check for updates that have already been applied, which should be filtered and ignored
            val data = packet.data
            if (data is UpdateData) {
                processUpdates(data, session)
            }

            // Resend the packet
            PacketSender.send(packet, session)
        }
    }

    override fun send(packet: BroadcastPacket, session: NetworkSession) {
        session.neighbours.values.filter {
            if (packet.trace.size == 1) true else it != packet.trace[packet.trace.size - 1]
        }.forEach {
            PacketSender.getCommService(it, session)?.sendPacket(packet.toString(), it)
        }
    }

    private fun processUpdates(data: UpdateData, session: NetworkSession) {
        for (update in data.updates) {
            val first = update.first ?: continue
            val second = update.second ?: continue

            when (update.action) {
                Update.CONNECTION_CREATED -> session.networkGraph.addEdge(first, second)
                Update.CONNECTION_DELETED -> session.networkGraph.removeEdge(first, second)
            }
        }
    }
}