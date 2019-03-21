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

                session.networkGraph.addDevice(source)
                session.networkGraph.addEdge(it, source)
                session.isInNetwork = true

                val graph = session.networkGraph.export()

                val responseData = HandshakeResponseData(graph)
                val response = HandshakeResponse(session.incrementPacketCount(), it, source, responseData)
                val updateData = UpdateData(mutableListOf(Update(it, source, Update.CONNECTION_CREATED)))

                PacketSender.send(response, session)
                PacketSender.send(BroadcastPacket.create(updateData, session), session)

                session.neighbours[source.id.toString()] = source
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