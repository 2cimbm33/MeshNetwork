package uni.cimbulka.network.packets.handlers

import uni.cimbulka.network.NetworkConstants
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.UpdateData
import uni.cimbulka.network.models.Update
import uni.cimbulka.network.packets.BroadcastPacket
import uni.cimbulka.network.packets.PacketSender

internal class BroadcastPacketHandler : PacketHandler<BroadcastPacket> {
    override fun receive(packet: BroadcastPacket, session: NetworkSession) {
        if (session.processedPackets.find { it.id == packet.id && it.source == packet.source } == null) {
            // Check for updates that have already been applied, which should be filtered and ignored
            var resend = true
            val updatesToResend = mutableListOf<Update>()

            val data = packet.data
            if (data is UpdateData) {
                updatesToResend.addAll(processUpdates(data, session))

                resend = if (updatesToResend.isNotEmpty()) {
                    data.updates.clear()
                    data.updates.addAll(updatesToResend)
                    packet.trace.size <= NetworkConstants.ZONE_SIZE
                } else {
                    false
                }
            }

            // Resend the packet
            if (resend) PacketSender.send(packet, session)
        }
    }

    override fun send(packet: BroadcastPacket, session: NetworkSession) {
        val data = packet.data
        when (data) {
            is UpdateData -> if (data.updates.isEmpty()) return
        }

        session.neighbours.values.filter {
            if (packet.trace.size == 1) true else it != packet.trace[packet.trace.size - 1]
        }.forEach {
            PacketSender.getCommService(it, session)?.sendPacket(packet.toString(), it)
        }
    }

    private fun processUpdates(data: UpdateData, session: NetworkSession): List<Update> {
        val pendingUpdates = mutableListOf(*data.updates.toTypedArray())
        val processedUpdates = session.processedUpdates
        val processedUpdatesKeys = processedUpdates.keys.sortedByDescending { it }
        var id = processedUpdatesKeys.firstOrNull()?.plus(1) ?: 1;
        val updatesToRun = mutableListOf<Update>()

        for (update in data.updates) {
            for (key in processedUpdatesKeys) {
                val check = processedUpdates[key] ?: continue
                if ((update.first == check.first || update.first == check.first) &&
                    (update.second == check.first || update.second == check.second)) {
                    if (update.action != check.action) {
                        updatesToRun.add(update)
                    }

                    pendingUpdates.remove(update)
                }
            }

            if (update in pendingUpdates) {
                updatesToRun.add(update)
                pendingUpdates.remove(update)
            }
        }

        updatesToRun.forEach {
            runUpdate(it, session)

            session.processedUpdates[id] = it
            id++
        }

        cleanProcessedUpdates(session)

        return updatesToRun
    }

    private fun runUpdate(update: Update, session: NetworkSession) {
        val first = update.first ?: return
        val second = update.second ?: return

        when (update.action) {
            Update.CONNECTION_CREATED -> session.networkGraph.addEdge(first, second)
            Update.CONNECTION_DELETED -> session.networkGraph.removeEdge(first, second)
        }
    }

    private fun cleanProcessedUpdates(session: NetworkSession) {
        val keys = session.processedUpdates.keys.sortedByDescending { it }
        val uniqueUpdates = mutableMapOf<Long, Update>()

        for (key in keys) {
            val update = session.processedUpdates[key] ?: continue
            var contains = false

            for (up in uniqueUpdates.values) {
                if ((update.first == up.first || update.first == up.first) &&
                    (update.second == up.first || update.second == up.second)) {

                    contains = true
                    break
                }
            }

            if (contains) {
                session.processedUpdates.remove(key)
            } else {
                uniqueUpdates[key] = update
            }
        }
    }
}