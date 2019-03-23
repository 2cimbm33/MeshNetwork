package uni.cimbulka.network.simulator.gui.models

import com.fasterxml.jackson.databind.JsonNode

data class Snapshot(var event: Event? = null, val nodes: MutableList<JsonNode> = mutableListOf())