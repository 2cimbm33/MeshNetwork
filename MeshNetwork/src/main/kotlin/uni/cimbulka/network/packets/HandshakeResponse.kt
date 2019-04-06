package uni.cimbulka.network.packets

import uni.cimbulka.network.data.HandshakeData
import uni.cimbulka.network.models.Device
import java.util.*

class HandshakeResponse(id: Int,
                        source: Device,
                        val recipient: Device,
                        data: HandshakeData,
                        timestamp: Long = Date().time) : BasePacket(id, source, data, timestamp)