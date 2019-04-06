package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.common.Position

class NetworkNode(var device: Device, position: Position = Position())
    : Node(device.id.toString(), position) {

    override val id: String
        get() = device.id.toString()

    @JsonIgnore
    var controller: NetworkController? = null
        set(value) {
            if (field == null) {
                field = value
            }
        }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Node -> this.id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return device.hashCode() + position.hashCode()
    }
}