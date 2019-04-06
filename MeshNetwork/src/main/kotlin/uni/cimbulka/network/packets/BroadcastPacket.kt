package uni.cimbulka.network.packets

import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.data.BaseData
import uni.cimbulka.network.models.Device
import java.util.*

class BroadcastPacket(id: Int, source: Device, data: BaseData, timestamp: Long) :
        BasePacket(id, source, data, timestamp) {

    constructor(id: Int, sender: Device, data: BaseData) :
            this(id, sender, data, Date().time)

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