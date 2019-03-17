package uni.cimbulka.network.listeners

import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.models.Device

interface NetworkCallbacks {
    fun onDataReceived(data: ApplicationData) {}
    fun onNetworkChanged(devices: List<Device>) {}
}
