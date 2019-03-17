package uni.cimbulka.network.listeners

import uni.cimbulka.network.models.Device

interface CommServiceCallbacks {
    fun onDiscoveryCompleted(devices: Array<Device>) {}
    fun onMessageReceived(packet: String) {}
    fun onDeviceDisconnected(address: String) {}
}
