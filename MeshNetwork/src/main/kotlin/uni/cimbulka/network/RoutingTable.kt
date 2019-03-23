package uni.cimbulka.network

import uni.cimbulka.network.models.Device
import java.util.*

internal data class RoutingTable(private val routingMap: Map<Device, Device>) {
    val timestamp = Date().time
    val keys: Set<Device>
        get() = routingMap.keys

    operator fun get(recipient: Device) = routingMap[recipient]
}
