package uni.cimbulka.network.data

import uni.cimbulka.network.models.Device

data class HandshakeData @JvmOverloads constructor(var graph: String = "",
                                                   val devices: MutableList<Device> = mutableListOf()) : BaseData()