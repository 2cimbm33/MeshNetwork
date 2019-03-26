package uni.cimbulka.network.packets

import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Route

class RouteDiscoveryResponse : BasePacket {

    val route: MutableList<Device>
    var recipient: Device?

    constructor() : super() {
        route = mutableListOf()
        recipient = null
    }

    @JvmOverloads
    constructor(id: Int, source: Device, timestamp: Long, recipient: Device? = null, route: MutableList<Device> = mutableListOf()) :
            super(id, source, EmptyData(), timestamp) {
        this.route = route
        this.recipient = recipient
    }
}