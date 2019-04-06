package uni.cimbulka.network.packets

import uni.cimbulka.network.data.EmptyData
import uni.cimbulka.network.models.Device

class RouteDiscoveryRequest(id: Int,
                            source: Device,
                            timestamp: Long,
                            var recipient: Device,
                            val requester: Device,
                            val target: Device,
                            val route: MutableList<Device> = mutableListOf()) : BasePacket(id, source, EmptyData(), timestamp)