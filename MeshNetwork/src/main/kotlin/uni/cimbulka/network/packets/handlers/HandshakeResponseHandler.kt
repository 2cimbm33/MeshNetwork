package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkGraph
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.HandshakeResponseData
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.HandshakeResponse
import uni.cimbulka.network.packets.PacketSender

internal class HandshakeResponseHandler : PacketHandler<HandshakeResponse> {
    override fun receive(packet: HandshakeResponse, session: NetworkSession) {
        println("Processing Handshake response")
        println("Check: ${session.localDevice.id == packet.recipient?.id}")
        if (session.localDevice.id == packet.recipient?.id) {
            println("Importing graph")
            val ( xml ) = packet.data as HandshakeResponseData
            session.networkGraph = NetworkGraph.import(xml, session)
            println("Marking isInNetwork as true")
            session.isInNetwork = true
            session.networkGraph.addDevice(session.localDevice)

            println("Creating updateData")
            val updateData = UpdateData()

            println("Starting the service")
            session.controller.startService()

            println("Neighbours: ${session.neighbours}")
            println("Devices in graph: ${session.networkGraph.devices}")
            println("Creating updates")
            session.neighbours.values.forEach {
                if (session.networkGraph.addEdge(session.localDevice, it)) {
                    updateData.updates.add(Update(session.localDevice, it, Update.CONNECTION_CREATED))
                    updateData.updates.add(Update(session.localDevice, it, Update.CONNECTION_CREATED))
                }
            }

            println("Sending update data")
            PacketSender.send(BroadcastPacket.create(updateData, session.controller), session)
        }
    }

    override fun send(packet: HandshakeResponse, session: NetworkSession) {
        packet.recipient?.let { PacketSender.getCommService(it, session)?.sendPacket(packet.toString(), it) }
    }
}