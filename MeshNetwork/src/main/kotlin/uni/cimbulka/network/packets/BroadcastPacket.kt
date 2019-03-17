package uni.cimbulka.network.packets

import com.fasterxml.jackson.databind.ser.Serializers
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.data.BaseData
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.packets.handlers.BroadcastPacketHandler
import uni.cimbulka.network.packets.handlers.PacketHandler
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
    }
}