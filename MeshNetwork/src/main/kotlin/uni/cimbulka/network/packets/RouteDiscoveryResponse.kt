package uni.cimbulka.network.packets

import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device

class RouteDiscoveryResponse(id: Int,
                             source: Device,
                             timestamp: Long,
                             var recipient: Device,
                             val route: MutableList<Device> = mutableListOf()) : BasePacket(id, source, EmptyData(), timestamp)