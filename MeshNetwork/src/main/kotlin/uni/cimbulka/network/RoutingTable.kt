package uni.cimbulka.network

import uni.cimbulka.network.models.Device
import java.util.*

internal data class RoutingTable(private val routingMap: Map<Device, Device>) {
    val timestamp = Date().time

    operator fun get(recipient: Device) = routingMap[recipient]
}
