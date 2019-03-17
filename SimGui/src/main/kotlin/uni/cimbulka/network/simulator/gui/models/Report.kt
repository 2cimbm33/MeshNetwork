package uni.cimbulka.network.simulator.gui.models

import com.fasterxml.jackson.databind.ObjectMapper

data class Report(var events: Map<String, Event> = emptyMap()) {

    companion object {
        fun fromJson(json: String): Report {
            return ObjectMapper().readValue(json, Report::class.java)
        }
    }
}