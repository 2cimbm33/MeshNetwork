package uni.cimbulka.network.simulator.gui.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property
import uni.cimbulka.network.simulator.gui.models.Snapshot
import uni.cimbulka.network.simulator.gui.views.GraphView

class SnapshotController : Controller() {
    val graphView: GraphView by inject()

    var name: String by property("")
    fun nameProperty() = getProperty(SnapshotController::name)

    var time: Double by property(Double.NaN)
    fun timeProperty() = getProperty(SnapshotController::time)

    var args: String by property("")
    fun argsProperty() = getProperty(SnapshotController::args)

    var nodes: String by property("")
    fun nodesProperty() = getProperty(SnapshotController::nodes)

    var connections: String by property("")
    fun connectionsProperty() = getProperty(SnapshotController::connections)

    var stats: String by property("")
    fun statsProperty() = getProperty(SnapshotController::stats)

    fun display(snapshot: Snapshot) {
        val mapper = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
        val event = snapshot.event ?: return

        val text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event.args)

        name = event.name
        time = event.time
        args = text

        val builder = StringBuilder()
        snapshot.nodes.forEach {
            builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            builder.appendln()
        }
        nodes = builder.toString()

        builder.clear()
        snapshot.connections.forEach {
            builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            builder.appendln()
        }
        connections = builder.toString()

        builder.clear()
        snapshot.aggregation.stats.forEach {
            builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            builder.appendln()
        }
        stats = builder.toString()

        graphView.draw(snapshot.nodes, snapshot.connections)
    }
}