package uni.cimbulka.network.data

import uni.cimbulka.network.models.Device
import uni.cimbulka.network.models.Update

data class UpdateData(val updates: MutableList<Update> = mutableListOf(), val newDevices: MutableList<Device> = mutableListOf()) :
        BaseData()