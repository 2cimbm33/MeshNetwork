package uni.cimbulka.network.packets

import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Route

class RouteDiscoveryResponse : BasePacket {

    var route: Route?
    var recipient: Device?

    constructor() : super() {
        route = null
        recipient = null
    }

    @JvmOverloads
    constructor(id: Int, source: Device, timestamp: Long, recipient: Device? = null, route: Route? = null) :
            super(id, source, EmptyData(), timestamp) {
        this.route = route
        this.recipient = recipient
    }
}