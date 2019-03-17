package uni.cimbulka.network.simulator.common

import javafx.geometry.Point2D

abstract class Node(val id: String, var position: Point2D = Point2D(0.0, 0.0)) {

    infix fun distanceFrom(node: Node) = position.distance(node.position)

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