package uni.cimbulka.network.packets

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.BaseData
import uni.cimbulka.network.models.Device
import java.util.*

class DataPacket(id: Int,
                 source: Device,
                 val recipients: MutableList<Device>,
                 data: BaseData,
                 timestamp: Long = Date().time) : BasePacket(id, source, data, timestamp) {

    constructor(id: Int, source: Device, recipient: Device, data: BaseData, timestamp: Long = Date().time) :
            this(id, source, mutableListOf(recipient), data, timestamp)

    companion object {
        @JvmStatic
        fun create(data: BaseData, controller: NetworkController, vararg recipients: Device) = DataPacket(
                controller.networkSession.incrementPacketCount(),
                controller.networkSession.localDevice,
                recipients.toMutableList(),
                data
        )

        internal fun create(data: BaseData, session: NetworkSession, vararg recipients: Device) = DataPacket(
                session.incrementPacketCount(),
                session.localDevice,
                recipients.toMutableList(),
                data
        )
    }
}