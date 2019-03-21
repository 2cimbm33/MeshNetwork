package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.HandshakeRequest
import uni.cimbulka.network.packets.PacketSender

class NeighborListener(private val session: NetworkSession) {
    fun onChanged(connected: List<Device>, disconnected: List<Device>) {
        val packet = if (!session.isInNetwork) {
            notInNetwork(connected)
        } else {
            inNetwork(connected, disconnected)
        }

        PacketSender.send(packet, session)
    }

    private fun notInNetwork(connected: List<Device>): HandshakeRequest {
        connected.forEach {
            session.neighbours[it.id.toString()] = it
        }

        return HandshakeRequest(session.incrementPacketCount(), session.localDevice, connected.random())
    }

    private fun inNetwork(connected: List<Device>, disconnected: List<Device>): BroadcastPacket {
        val data = UpdateData()

        for (device in connected) {
            val update = connect(device) ?: continue
            data.updates.add(update)
        }

        for (device in disconnected) {
            val update = disconnect(device) ?: continue
            data.updates.add(update)
        }

        return BroadcastPacket.create(data, session)
    }

    private fun connect(device: Device): Update? {
        if (device !in session.networkGraph.devices) {
            session.networkGraph.addDevice(device)
        }

        val result = session.networkGraph.addEdge(session.localDevice, device)

        return if (result) {
            session.neighbours[device.id.toString()] = device
            Update(session.localDevice, device, Update.CONNECTION_CREATED)
        } else {
            null
        }
    }

    private fun disconnect(device: Device): Update? {
        val result = session.networkGraph.removeEdge(session.localDevice, device)

        return if (result) {
            session.neighbours.remove(device.id.toString())
            Update(session.localDevice, device, Update.CONNECTION_DELETED)
        } else {
            null
        }
    }
}