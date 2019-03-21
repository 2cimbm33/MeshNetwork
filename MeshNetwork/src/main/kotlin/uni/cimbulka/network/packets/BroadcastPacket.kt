package uni.cimbulka.network.packets

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.BaseData
import uni.cimbulka.network.models.Device
import java.util.*

class BroadcastPacket : BasePacket {
    constructor() : super()

    constructor(id: Int, sender: Device, data: BaseData, timestamp: Long) :
            super(id, sender, data, timestamp)

    constructor(id: Int, sender: Device, data: BaseData) :
            super(id, sender, data, Date().time)

    companion object {
        @JvmStatic
        fun create(data: BaseData, controller: NetworkController) = BroadcastPacket(
                controller.networkSession.incrementPacketCount(),
                controller.networkSession.localDevice,
                data
        )

        internal fun create(data: BaseData, session: NetworkSession) = BroadcastPacket(
                session.incrementPacketCount(),
                session.localDevice,
                data
        )
    }
}