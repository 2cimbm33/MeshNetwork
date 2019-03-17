package uni.cimbulka.network.simulator.gui.models

import com.fasterxml.jackson.databind.JsonNode

data class Event(var time: Double = 0.0, var name: String = "", var args: JsonNode? = null)