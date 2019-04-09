package uni.cimbulka.network.simulator.gui.database

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.beans.property.ReadOnlyProperty
import org.neo4j.driver.v1.types.Node
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.MainController
import uni.cimbulka.network.simulator.gui.models.Snapshot

class SnapshotDao : Controller() {
    private val mainController: MainController by inject()
    private val driver = Database.driver
    private val mapper = ObjectMapper()

    var snapshot: Snapshot? by property(value = null)
        private set
    fun snapshotProperty() = getProperty(SnapshotDao::snapshot) as ReadOnlyProperty<Snapshot?>

    fun getSnapshot(id: Int) {
/*        runAsync {
            var snapshot: Snapshot? = null
            driver.session().run {
                readTransaction { tx ->
                    val rs = tx.run("MATCH (sim:Simulation)-->(snap:Snapshot), (snap)-->(e:Event), (snap)-[r:CONTAINS]->(n:Node) " +
                            "WHERE sim.simId = \$simId AND snap.id = \$id " +
                            "RETURN e, r, n", mapOf("simId" to mainController.simId, "id" to id))

                    val records = rs.list()
                    val first = records.firstOrNull() ?: return@readTransaction
                    val eventNode = first["e"].asNode()
                    val avgEventTime = eventNode["time"].asDouble()
                    val eventArgs = mapper.readTree(eventNode["args"].asString())

                    val event = Event(avgEventTime, getEventName(eventNode), eventArgs)
                    val nodes = mutableListOf<PositionNode>()
                    val connections = mutableListOf<Connection>()

                    for (record in records) {
                        val n = record["n"].asNode() ?: continue
                        val r = record["r"].asRelationship() ?: continue

                        val node = PositionNode(n["id"].asString(), n["name"].asString(), Position(r["x"].asDouble(), r["y"].asDouble()))
                        nodes.add(node)

                        for (c in mapper.readValue(r["conn"].asString(), List::class.java)) {
                            val conn = Connection(node.id, c as String)
                            if (conn !in connections) {
                                connections.add(conn)
                            }
                        }
                    }
                    snapshot = Snapshot(id, event, nodes, connections)
                }

                snapshot
            }
        } ui {
            snapshot = it
        }*/
    }

    private fun getEventName(event: Node): String {
        for (label in event.labels()) {
            if (label != "Event") {
                return label
            }
        }

        return ""
    }
}