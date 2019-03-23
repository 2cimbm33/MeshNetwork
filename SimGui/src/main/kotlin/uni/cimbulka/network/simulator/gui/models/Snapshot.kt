package uni.cimbulka.network.simulator.gui.models

import uni.cimbulka.network.simulator.mesh.reporting.Aggregation
import uni.cimbulka.network.simulator.mesh.reporting.Connection
import uni.cimbulka.network.simulator.mesh.reporting.SimpleNode

data class Snapshot(var event: Event? = null,
                    val nodes: List<SimpleNode> = emptyList(),
                    val connections: List<Connection> = emptyList(),
                    var aggregation: Aggregation = Aggregation())