package uni.cimbulka.network.simulator.gui.models

import uni.cimbulka.network.simulator.common.Position

class PositionNode(id: String, name: String, var position: Position) : Node(id, name) {
    fun copy(id: String? = null, name: String? = null, position: Position? = null): PositionNode {
        return PositionNode(id ?: this.id, name ?: this.name, position ?: this.position)
    }
}