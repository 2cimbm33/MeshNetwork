package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.PacketSender

class NeighborListener(private val session: NetworkSession) {
    fun onChanged(connected: List<Device>, disconnected: List<Device>) {
        val data = UpdateData()

        for (device in connected) {
            val update = connect(device) ?: continue
            data.updates.add(update)
        }

        for (device in disconnected) {
            val update = disconnect(device) ?: continue
            data.updates.add(update)
        }

        PacketSender.send(BroadcastPacket.create(data, session.controller), session)
    }

    private fun connect(device: Device): Update? {
        if (device !in session.networkGraph.devices) {
            session.networkGraph.addDevice(device)
        }

        val result = session.networkGraph.addEdge(session.localDevice, device)

        return if (result) {
            Update(session.localDevice, device, Update.CONNECTION_CREATED)
        } else {
            null
        }
    }

    private fun disconnect(device: Device): Update? {
        val result = session.networkGraph.removeEdge(session.localDevice, device)

        return if (result) {
            Update(session.localDevice, device, Update.CONNECTION_DELETED)
        } else {
            null
        }
    }
}