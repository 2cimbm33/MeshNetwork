package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.annotation.JsonIgnore
import javafx.geometry.Point2D
import uni.cimbulka.network.NetworkController
import uni.cimbulka.network.models.Device
import uni.cimbulka.network.simulator.common.Node

class NetworkNode(val device: Device, position: Point2D = Point2D(0.0, 0.0))
    : Node(device.id.toString(), position) {

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