package uni.cimbulka.network

import uni.cimbulka.network.listeners.CommServiceCallbacks
import uni.cimbulka.network.models.Device

abstract class CommService(val friendlyName: String) {
    abstract val isReady: Boolean
    abstract val neighbors: List<Device>
    abstract var serviceCallbacks: CommServiceCallbacks?

    abstract fun startDiscovery(inNetwork: Boolean): Boolean
    abstract fun validateDevice(device: Device): Boolean
    abstract fun sendPacket(packet: String, recipient: Device)
    abstract fun startService()
    abstract fun stopService()
}
