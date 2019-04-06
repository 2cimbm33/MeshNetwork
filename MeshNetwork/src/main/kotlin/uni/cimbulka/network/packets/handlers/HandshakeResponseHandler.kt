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
        println("Importing graph")
        val ( xml, devices ) = packet.data as HandshakeData
        session.networkGraph.merge(xml, packet.source, session)
        println("Marking inNetwork as true")
        session.isInNetwork = true

        val updateData = UpdateData()
        devices.forEach {
            if (it !in session.allDevices) {
                session.allDevices.add(it)
                updateData.newDevices.add(it)
            }
        }

        updateData.updates.add(Update(session.localDevice to packet.source, Update.CONNECTION_CREATED))
        PacketSender.send(BroadcastPacket.create(updateData, session), session)
    }

    override fun send(packet: HandshakeResponse, session: NetworkSession) {
        packet.recipient.let { PacketSender.getCommService(it, session)?.sendPacket(packet.toString(), it) }
    }
}