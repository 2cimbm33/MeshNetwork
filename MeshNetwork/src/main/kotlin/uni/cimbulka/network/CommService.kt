package uni.cimbulka.network

import uni.cimbulka.network.listeners.CommServiceCallbacks
import uni.cimbulka.network.models.Device

abstract class CommService(val friendlyName: String) {
    abstract val isReady: Boolean
    abstract val neighbors: List<Device>
    abstract var serviceCallbacks: CommServiceCallbacks?

    abstract fun sendPacket(packet: String, recipient: Device)
    abstract fun startScanning()
    abstract fun stopScanning()
    abstract fun startService()
    abstract fun stopService()
}
