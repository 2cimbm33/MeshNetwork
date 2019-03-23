package uni.cimbulka.network.listeners

import uni.cimbulka.network.models.Device

interface CommServiceCallbacks {
    fun onNeighborsChanged(connected: List<Device> = emptyList(), disconnected: List<Device> = emptyList())
    fun addNeighbor(neighbor: Device)
    fun removeNeighbor(neighbor: Device)

    fun onMessageReceived(packet: String)
}
