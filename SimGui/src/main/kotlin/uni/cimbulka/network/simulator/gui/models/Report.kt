package uni.cimbulka.network.simulator.gui.models

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

data class Report(var events: Map<String, JsonNode> = emptyMap()) {

    companion object {
        fun fromJson(json: String): Report {
            return ObjectMapper().readValue(json, Report::class.java)
        }
    }
}