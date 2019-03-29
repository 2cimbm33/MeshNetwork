package uni.cimbulka.network.simulator.physical

import javafx.geometry.Point2D
import uni.cimbulka.network.simulator.Constants
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.common.Position

class PhysicalLayer(val height: Double = 100.0, val width: Double = 100.0) {
    private val nodes = mutableListOf<Node>()
    val keys: List<String>
        get() {
            val result = mutableListOf<String>()
            nodes.forEach { result.add(it.id) }
            return result.toList()
        }

    fun addNode(node: Node): Boolean {
        if (node in nodes) return false
        return nodes.add(node)
    }

    fun removeNode(id: String): Boolean {
        val temp = nodes.firstOrNull { it.id == id } ?: return false
        return nodes.remove(temp)
    }

    operator fun get(id: String) = nodes.firstOrNull { it.id == id }

    fun getAll(): List<Node> = nodes.toList()

    fun getDistance(first: String, second: String): Double {
        val firstNode = nodes.firstOrNull { it.id == first } ?: return Double.NaN
        val secondNode = nodes.firstOrNull { it.id == second } ?: return Double.NaN

        return firstNode.distanceFrom(secondNode)
    }

    fun inRange(first: String, second: String): Boolean {
        return getDistance(first, second) <= Constants.Bluetooth.BLUETOOTH_RANGE
    }

    fun moveNode(id: String, dx: Double, dy: Double) {
        nodes.first { it.id == id }.apply {
            val x = position.x + dx
            val y = position.y + dy

            position = Position(
                    when {
                        x > width -> width
                        x < 0.0 -> 0.0
                        else -> x
                    },
                    when {
                        y > height -> height
                        y < 0.0 -> 0.0
                        else -> y
                    }
            )
        }
    }
}