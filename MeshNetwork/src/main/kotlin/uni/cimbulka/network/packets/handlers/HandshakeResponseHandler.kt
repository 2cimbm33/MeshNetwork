package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.HandshakeData
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.HandshakeResponse
import uni.cimbulka.network.packets.PacketSender

internal class HandshakeResponseHandler : PacketHandler<HandshakeResponse> {
    override fun receive(packet: HandshakeResponse, session: NetworkSession) {
        val ( xml, devices ) = packet.data as HandshakeData
        session.networkGraph.addEdge(session.localDevice, packet.source)
        session.networkGraph.merge(xml, packet.source, session)
        session.isInNetwork = true

        val updateData = UpdateData()
        devices.forEach {
            if (it !in session.allDevices) {
                session.allDevices.add(it)
                updateData.newDevices.add(it)
            }
        }
        if (devices.isNotEmpty()) {
            session.networkCallbacks?.onNetworkChanged(session.allDevices.toList())
        }

        updateData.updates.add(Update(session.localDevice to packet.source, Update.CONNECTION_CREATED))
        PacketSender.send(BroadcastPacket.create(updateData, session).apply { exclude = listOf(packet.source) }, session)
    }

    override fun send(packet: HandshakeResponse, session: NetworkSession) {
        packet.recipient.let { PacketSender.getCommService(it, session)?.sendPacket(packet.toString(), it) }
    }
}