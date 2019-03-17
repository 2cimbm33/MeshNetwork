package uni.cimbulka.network.simulator.mesh

import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.physical.PhysicalLayer

class SimulationSnapshot(event: Event<*>, physicalLayer: PhysicalLayer) {
    val event = event
    val nodes: List<NetworkNode>
    val connections: List<Connection>

    init {
        nodes = getNodes(physicalLayer)
        connections = getConnections(nodes, physicalLayer)
    }

    private fun getNodes(phy: PhysicalLayer): List<NetworkNode> {
        val result = mutableListOf<NetworkNode>()

        for (key in phy.keys) {
            val t = phy[key] ?: continue
            val n = t as? NetworkNode ?: continue
            result.add(n)
        }

        return result
    }

    private fun getConnections(nodes: List<NetworkNode>, phy: PhysicalLayer): List<Connection> {
        val result = mutableListOf<Connection>()

        nodes.forEach { node ->
            nodes.forEach {
                if (node != it && phy.inRange(node.id, it.id)) {
                    val conn = Connection(node.id, it.id)
                    if (conn !in result) {
                        result.add(conn)
                    }
                }
            }
        }

        return result
    }
}