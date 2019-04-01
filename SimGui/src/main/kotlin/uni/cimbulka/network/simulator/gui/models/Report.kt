package uni.cimbulka.network.simulator.gui.models

import com.fasterxml.jackson.databind.ObjectMapper
import uni.cimbulka.network.simulator.mesh.reporting.Aggregation

data class Report(var events: Map<String, Snapshot> = emptyMap(),
                  var nodes: List<Node> = emptyList(),
                  var aggregation: Aggregation = Aggregation()) {

    companion object {
        fun fromJson(json: String): Report {
            return ObjectMapper().readValue(json, Report::class.java)
        }
    }
}