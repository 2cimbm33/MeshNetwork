package uni.cimbulka.network.listeners

import uni.cimbulka.network.models.Device

interface CommServiceCallbacks {
    fun onNeighborsChanged(connected: List<Device>, disconnected: List<Device>)
    fun onMessageReceived(packet: String)
}
