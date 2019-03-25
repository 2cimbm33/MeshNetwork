package uni.cimbulka.network.packets

import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Route

class RouteDiscoveryRequest : BasePacket {

    var recipient: Device?
    var requester: Device?
    var target: Device?
    var route: Route?

    constructor() : super() {
        recipient = null
        requester = null
        target = null
        route = null
    }

    @JvmOverloads
    constructor(id: Int, source: Device?, timestamp: Long, recipient: Device? = null,
                requester: Device? = null, target: Device? = null, route: Route? = null) :
    super(id, source, EmptyData(), timestamp) {
        this.recipient = recipient
        this.requester = requester
        this.target = target
        this.route = route
    }

    fun copy() =  RouteDiscoveryRequest(
            id, source, timestamp, recipient, requester, target, Route().apply {
        this@RouteDiscoveryRequest.route?.segments?.forEach {
            segments.add(it.copy())
        }
    }
    )
}