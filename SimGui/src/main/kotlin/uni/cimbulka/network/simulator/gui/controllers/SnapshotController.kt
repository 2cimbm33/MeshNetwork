package uni.cimbulka.network.simulator.gui.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javafx.geometry.Dimension2D
import tornadofx.*
import uni.cimbulka.network.simulator.gui.database.SnapshotDao
import uni.cimbulka.network.simulator.gui.events.RedrawCanvas
import uni.cimbulka.network.simulator.gui.models.Snapshot

class SnapshotController : Controller() {
    private val snapDao: SnapshotDao by inject()
    private var dimensions = Dimension2D(100.0, 100.0)

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

    init {
        snapDao.snapshotProperty().onChange { snapshot ->
            snapshot?.let {
                display(it)
            }
        }
    }

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

        fire(RedrawCanvas(snapshot.nodes, snapshot.connections, dimensions))
    }
}