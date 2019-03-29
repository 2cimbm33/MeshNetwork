package uni.cimbulka.network.simulator.gui.models

import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.mesh.reporting.Aggregation
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.mesh.reporting.SimpleNode

data class Snapshot(var event: Event? = null,
                    val nodes: List<NetworkNode> = emptyList(),
                    val connections: List<Connection> = emptyList(),
                    var aggregation: Aggregation = Aggregation())