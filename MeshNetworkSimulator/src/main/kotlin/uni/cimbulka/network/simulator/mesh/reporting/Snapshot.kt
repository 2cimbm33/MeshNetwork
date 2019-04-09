package uni.cimbulka.network.simulator.mesh.reporting

import com.fasterxml.jackson.databind.JsonNode
import uni.cimbulka.network.simulator.common.Position

data class Snapshot(val index: Int,
                    val simId: String,
                    val time: Double,
                    val eventName: String,
                    val eventArgs: JsonNode,
                    val nodeId: String,
                    val position: Position?,
                    val connections: List<String>,
                    val nodesInRange: List<String>)