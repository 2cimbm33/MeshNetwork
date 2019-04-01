package uni.cimbulka.network.simulator.gui.models

import uni.cimbulka.network.simulator.mesh.reporting.Aggregation
import uni.cimbulka.network.simulator.mesh.reporting.Connection

data class Snapshot(var id: Int? = null,
                    var event: Event? = null,
                    val nodes: List<PositionNode> = emptyList(),
                    val connections: List<Connection> = emptyList(),
                    var aggregation: Aggregation = Aggregation())