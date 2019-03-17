package uni.cimbulka.network.packets

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.data.BaseData
import uni.cimbulka.network.models.Device
import java.util.*

class DataPacket : BasePacket {

    val recipients: MutableList<Device>

    constructor() : super() {
        this.recipients = mutableListOf()
    }

    constructor(id: Int, sender: Device, recipients: MutableList<Device>, data: BaseData, timestamp: Long = Date().time) :
            super (id, sender, data, timestamp) {
        this.recipients = recipients
    }

    constructor(id: Int, sender: Device, recipient: Device, data: BaseData, timestamp: Long) :
            super (id, sender, data, timestamp) {
        recipients = mutableListOf(recipient)
    }

    constructor(id: Int, sender: Device, recipient: Device, data: BaseData) :
            super (id, sender, data, Date().time) {
        recipients = mutableListOf(recipient)
    }

    companion object {
        @JvmStatic
        fun create(data: BaseData, vararg recipients: Device, controller: NetworkController) = DataPacket(
                controller.networkSession.incrementPacketCount(),
                controller.networkSession.localDevice,
                recipients.toMutableList(),
                data
        )
    }
}