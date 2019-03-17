package uni.cimbulka.network.data

import uni.cimbulka.network.models.Update

data class UpdateData @JvmOverloads constructor(val updates: MutableList<Update> = mutableListOf()) :
        BaseData()