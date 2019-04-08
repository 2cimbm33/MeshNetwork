package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.PacketSender

class NeighborListener(private val session: NetworkSession) {
    fun onNeighborDisconnected(vararg neighbor: Device) {
        val updateData = UpdateData()

        for (n in neighbor) {
            val update = disconnect(n) ?: continue
            updateData.updates.add(update)
        }

        for (device in session.allDevices.filter { it !in session.neighbours.values }) {
            synchronized(device) {
                if (device != session.localDevice) {
                    for (service in session.services) {
                        val connString = device.communications[service::class.qualifiedName] ?: continue
                        if (service.connect(connString)) {
                            val update = connect(device) ?: continue
                            updateData.updates.add(update)
                        }
                    }
                }
            }
        }

        val packet = BroadcastPacket.create(updateData, session)
        PacketSender.send(packet, session)
    }

    private fun disconnect(device: Device): Update? {
        val result = session.networkGraph.removeEdge(session.localDevice, device)

        return if (result) {
            updateLongDistanceVectors(device)
            val update = Update(session.localDevice, device, Update.CONNECTION_DELETED)

            val id = session.processedUpdates.keys.sortedDescending().firstOrNull()?.plus(1) ?: 1
            session.processedUpdates[id] = update

            update
        } else {
            null
        }
    }

    private fun connect(device: Device): Update? {
        val result = session.networkGraph.removeEdge(session.localDevice, device)

        return if (result) {
            val update = Update(session.localDevice, device, Update.CONNECTION_CREATED)

            val id = session.processedUpdates.keys.sortedDescending().firstOrNull()?.plus(1) ?: 1
            session.processedUpdates[id] = update

            update
        } else {
            null
        }
    }

    private fun updateLongDistanceVectors(device: Device) {
        val keysToRemove = mutableListOf<Device>()

        for ((key, value) in session.longDistanceVectors) {
            if (value == device) {
                keysToRemove.add(key)
            }
        }

        keysToRemove.forEach { session.longDistanceVectors.remove(it) }
    }
}