package uni.cimbulka.network.simulator.mesh.reporting

import uni.cimbulka.network.simulator.bluetooth.AdapterPool
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.physical.PhysicalLayer

class SimulationSnapshot(val event: Event<*>, val aggregation: Aggregation, physicalLayer: PhysicalLayer) {
    val nodes: List<SimpleNode>
    val connections: List<Connection>

    init {
        nodes = getNodes(physicalLayer)
        connections = getConnections(nodes)
    }

    private fun getNodes(phy: PhysicalLayer): List<SimpleNode> {
        val result = mutableListOf<SimpleNode>()

        for (key in phy.keys) {
            val node = phy[key] as? NetworkNode ?: continue
            result.add(SimpleNode(node.id, Position(node.position.x, node.position.y)))
        }

        return result
    }

    private fun getConnections(nodes: List<SimpleNode>): List<Connection> {
        val result = mutableListOf<Connection>()


        for (node in nodes) {
            val adapter = AdapterPool.adapters[node.id] ?: continue
            adapter.connections.forEach { id, _ ->
                val conn = Connection(node.id, id)
                if (conn !in result) {
                    result.add(conn)
                }
            }
        }

        return result
    }
}