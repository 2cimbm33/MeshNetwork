package uni.cimbulka.network.simulator.common

import javafx.geometry.Point2D

abstract class Node(open val id: String, var position: Position = Position()) {

    infix fun distanceFrom(node: Node): Double {
        return Point2D(position.x, position.y).distance(node.position.x, node.position.y)
    }

    override fun equals(other: Any?): Boolean {
        return when(other) {
            is Node -> return id == other.id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}