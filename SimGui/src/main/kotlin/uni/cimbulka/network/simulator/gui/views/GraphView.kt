package uni.cimbulka.network.simulator.gui.views

import javafx.geometry.Point2D
import javafx.scene.paint.Color
import tornadofx.*
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.mesh.reporting.SimpleNode

class GraphView : View("Graph view") {
    private val group = group()

    override val root = pane {
        add(group)
    }

    fun draw(nodes: List<NetworkNode>, connections: List<Connection>) {
        val (heightMinMax, widthMinMax) = findMinMax(nodes)
        val dimensions = root.height to root.width

        group.run {
            children.clear()

            nodes.forEach {
                circle {
                    radius = 10.0
                    centerX = it.position.x * 10
                    centerY = it.position.y * 10
                    fill = Color.BLUE
                    translateZ = 1.0
                }
            }

            for (connection in connections) {
                val first = nodes.firstOrNull { it.id == connection.first } ?: continue
                val second = nodes.firstOrNull { it.id == connection.second } ?: continue

                line {
                    startX = first.position.x * 10
                    startY = first.position.y * 10
                    endX = second.position.x * 10
                    endY = second.position.y * 10
                }
            }

        }

    }

    private fun calcActualPosition(node: SimpleNode,
                                   dimensions: Pair<Double, Double>,
                                   heightMinMax: Pair<Double, Double>,
                                   widthMinMax: Pair<Double, Double>): Point2D {

        return Point2D(0.0, 0.0)
    }

    private fun findMinMax(nodes: List<NetworkNode>): Pair<Pair<Double, Double>, Pair<Double, Double>> {
        var xmin = Double.MAX_VALUE
        var xmax = Double.MIN_VALUE
        var ymin = Double.MAX_VALUE
        var ymax = Double.MIN_VALUE

        for (node in nodes) {
            val x = node.position.x
            val y = node.position.y

            if (x < xmin) xmin = x
            else if (x > xmax) xmax = x
            if (y < ymin) ymin = y
            else if (y > ymax) ymax = y
        }

        val heightMinMax = (ymin - 10) to (ymax + 10)
        val widthMinMax = (xmin - 10) to (xmax + 10)
        return heightMinMax to widthMinMax
    }
}