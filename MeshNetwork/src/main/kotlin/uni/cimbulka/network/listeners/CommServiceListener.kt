package uni.cimbulka.network.listeners

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.models.Device

internal class CommServiceListener(controller: NetworkController) : CommServiceCallbacks {
    private val neighborListener = NeighborListener(controller.networkSession)
    private val messageListener = MessageListener(controller.networkSession)

    override fun onNeighborsChanged(connected: List<Device>, disconnected: List<Device>) {
        neighborListener.onChanged(connected, disconnected)
    }

    override fun addNeighbor(neighbor: Device) {
        neighborListener.add(neighbor)
    }

    override fun removeNeighbor(neighbor: Device) {
        neighborListener.remove(neighbor)
    }

    override fun onMessageReceived(packet: String) {
        println("\nCommServiceListener:onMessageReceived\n")
        messageListener.onMessageReceived(packet)
    }
}