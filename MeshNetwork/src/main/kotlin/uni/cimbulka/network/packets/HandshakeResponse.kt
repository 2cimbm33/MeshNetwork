package uni.cimbulka.network.packets

import uni.cimbulka.network.data.HandshakeResponseData
import uni.cimbulka.network.models.Device
import java.util.*

@JvmSuppressWildcards
class HandshakeResponse : BasePacket {

    var recipient: Device?

    constructor() : super() {
        recipient = null
    }

    constructor(id: Int, sender: Device, recipient: Device, graph: HandshakeResponseData, timestamp: Long = Date().time) :
            super (id, sender, graph, timestamp) {
        this.recipient = recipient
    }
}
