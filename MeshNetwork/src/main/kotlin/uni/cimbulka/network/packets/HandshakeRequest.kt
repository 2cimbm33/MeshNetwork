package uni.cimbulka.network.packets

import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device
import java.util.*

@JvmSuppressWildcards
class HandshakeRequest  : BasePacket {

    var recipient: Device?

    constructor() : super() {
        recipient = null
    }

    constructor(id: Int, sender: Device, recipient: Device, timestamp: Long = Date().time) :
          super (id, sender, EmptyData(), timestamp) {
        this.recipient = recipient
    }
}
