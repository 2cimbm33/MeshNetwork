package uni.cimbulka.network.simulator.mesh.reporting

import com.fasterxml.jackson.databind.JsonNode
import uni.cimbulka.network.simulator.common.Node

class Report(var events: MutableMap<String, JsonNode> = mutableMapOf(),
             var nodes: MutableList<Node> = mutableListOf(),
             var aggregation: Aggregation = Aggregation())