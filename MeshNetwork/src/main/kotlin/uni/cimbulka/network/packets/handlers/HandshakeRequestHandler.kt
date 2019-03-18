package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.HandshakeResponseData
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.HandshakeRequest
import uni.cimbulka.network.packets.HandshakeResponse
import uni.cimbulka.network.packets.PacketSender

internal class HandshakeRequestHandler : PacketHandler<HandshakeRequest> {
    override fun receive(packet: HandshakeRequest, session: NetworkSession) {
        println("Processing Handshake request")
        packet.recipient?.let {
            if (session.localDevice.id == it.id) {
                val source = packet.source ?: return

                session.networkGraph.addDevice(it)
                session.networkGraph.addEdge(session.localDevice, it)
                session.isInNetwork = true

                val graph = session.networkGraph.export()

                val responseData = HandshakeResponseData(graph)
                val response = HandshakeResponse(session.incrementPacketCount(), session.localDevice, source, responseData)
                val updateData = UpdateData(mutableListOf(Update(session.localDevice, it, Update.CONNECTION_CREATED)))

                PacketSender.send(response, session)
                PacketSender.send(BroadcastPacket.create(updateData, session.controller), session)

                session.neighbours[it.id.toString()] = it
            }
        }
    }

    override fun send(packet: HandshakeRequest, session: NetworkSession) {
        packet.recipient?.let {
            val service = PacketSender.getCommService(it, session)
            service?.sendPacket(packet.toString(), it)
        }
    }
}