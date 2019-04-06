package uni.cimbulka.network.packets

import uni.cimbulka.network.data.HandshakeData
import uni.cimbulka.network.models.Device
import java.util.*

@JvmSuppressWildcards
class HandshakeRequest(id: Int,
                       source: Device,
                       data: HandshakeData,
                       timestamp: Long = Date().time) : BasePacket(id, source, data, timestamp)
