package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.DataProcessor
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.DataPacket
import uni.cimbulka.network.packets.PacketSender

internal class DataPacketHandler : PacketHandler<DataPacket> {
    override fun receive(packet: DataPacket, session: NetworkSession) {
        println("Processing Data packet")
        if (session.localDevice in packet.recipients) {
            DataProcessor.process(packet.data, session)
        }

        PacketSender.send(packet, session)
    }

    override fun send(packet: DataPacket, session: NetworkSession) {
        packet.source?.let {
            val packets = mutableMapOf<Device, DataPacket>()

            for (recipient in packet.recipients) {
                if (recipient != session.localDevice) {
                    val nextNode: Device = if (session.routingTable[recipient] == null) {
                        var result: Device? = null

                        for ((key, value) in session.longDistanceVectors) {
                            if (recipient in value) {
                                result = session.routingTable[key]
                                break
                            }
                        }

                        result
                    } else {
                        session.routingTable[recipient] ?: continue
                    } ?: break


                    if (nextNode in packets) {
                        packets[nextNode]?.recipients?.add(recipient)
                    } else {
                        val dp = DataPacket(packet.id, it, recipient, packet.data, packet.timestamp).apply {
                            trace = packet.trace
                        }
                        packets[nextNode] = dp
                    }
                }
            }

            packets.forEach { r, p -> PacketSender.getCommService(r, session)?.sendPacket(p.toString(), r) }
        }
    }
}