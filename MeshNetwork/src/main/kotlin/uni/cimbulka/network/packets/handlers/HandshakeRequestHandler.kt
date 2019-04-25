package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.HandshakeData
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.HandshakeRequest
import uni.cimbulka.network.packets.HandshakeResponse
import uni.cimbulka.network.packets.PacketSender

internal class HandshakeRequestHandler : PacketHandler<HandshakeRequest> {
    override fun receive(packet: HandshakeRequest, session: NetworkSession) {
        val source = packet.source
        val data = packet.data as HandshakeData

        session.networkGraph.addEdge(session.localDevice, source)
        session.networkGraph.merge(data.graph, source, session)
        session.isInNetwork = true

        val updateData = UpdateData()
        data.devices.forEach {
            if (it !in session.allDevices) {
                session.allDevices.add(it)
                updateData.newDevices.add(it)
            }
        }
        if (data.devices.isNotEmpty()) {
            session.networkCallbacks?.onNetworkChanged(session.allDevices.toList())
        }

        val graph = session.networkGraph.export()

        val responseData = HandshakeData(graph, session.allDevices)
        val response = HandshakeResponse(session.incrementPacketCount(), session.localDevice, source, responseData)
        updateData.updates.add(Update(session.localDevice, source, Update.CONNECTION_CREATED))

        PacketSender.send(response, session)
        PacketSender.send(BroadcastPacket.create(updateData, session).apply { exclude = listOf(source) }, session)
    }

    override fun send(packet: HandshakeRequest, session: NetworkSession) {
        throw RuntimeException("Handshake request can't be sent through the packet sender")
    }
}