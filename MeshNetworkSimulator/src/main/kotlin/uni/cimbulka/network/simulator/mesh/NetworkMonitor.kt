package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.neo4j.driver.v1.Driver
import uni.cimbulka.network.packets.*
import uni.cimbulka.network.simulator.bluetooth.events.EndDiscoveryEvent
import uni.cimbulka.network.simulator.bluetooth.events.ReceivePacketEvent
import uni.cimbulka.network.simulator.bluetooth.events.SendPacketEvent
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.mesh.reporting.Report
import uni.cimbulka.network.simulator.mesh.reporting.SimulationSnapshot
import uni.cimbulka.network.simulator.mesh.reporting.Statistics
import uni.cimbulka.network.simulator.physical.PhysicalLayer

@Suppress("Duplicates")
class NetworkMonitor(val simulationId: String,
                     val physicalLayer: PhysicalLayer,
                     simulatorType: String,
                     private val driver: Driver) : MonitorInterface {

    private val report = Report()
    private val mapper = ObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    private var numberOfEvents = 0

    internal var callbacks: SimulationCallbacks? = null

    init {
        driver.session().apply {
            writeTransaction { tx ->
                tx.run("CREATE (n:Simulation:$simulatorType {simId: \$simId})",
                        mapOf("simId" to simulationId))
            }
        }
    }

    override fun record(event: EventInterface) {
        if (event is Event<*>) {
            when (event) {
                is EndDiscoveryEvent -> {
                    val id = event.args.adapter.node.id
                    getStats(id).discoveriesCompleted++
                }

                is SendPacketEvent -> {
                    val id = event.args.adapter.node.id
                    val stats = getStats(id)
                    val packet = BasePacket.fromJson(event.args.packet.data)

                    when (packet) {
                        is BroadcastPacket -> stats.broadcastPacketSent++
                        is DataPacket -> stats.dataPacketSent++
                        is HandshakeRequest -> stats.handshakeRequestsSent++
                        is HandshakeResponse -> stats.handshakeResponsesSent++
                        is RouteDiscoveryRequest -> stats.routeDiscoveryRequestsSent++
                        is RouteDiscoveryResponse -> stats.routeDiscoveryResponsesSent++
                    }

                    stats.totalPacketsSent++
                }

                is ReceivePacketEvent -> {
                    val id = event.args.adapter.node.id
                    val stats = getStats(id)
                    val packet = BasePacket.fromJson(event.args.packet.data)

                    when (packet) {
                        is BroadcastPacket -> stats.broadcastPacketReceived++
                        is DataPacket -> stats.dataPacketReceived++
                        is HandshakeRequest -> stats.handshakeRequestsReceived++
                        is HandshakeResponse -> stats.handshakeResponsesReceived++
                        is RouteDiscoveryRequest -> stats.routeDiscoveryRequestsReceived++
                        is RouteDiscoveryResponse -> stats.routeDiscoveryResponsesReceived++
                    }

                    stats.totalPacketsReceived++
                }
            }

            val snapshot = SimulationSnapshot(event, report.aggregation, physicalLayer)
            val json = mapper.valueToTree<JsonNode>(snapshot)

            report.events["[$numberOfEvents] [${event.time}] ${event.name}"] = json
            //println("\n[$numberOfEvents] [${event.time}]:\n${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)}\n")

            saveSnapshot(snapshot)

            numberOfEvents++
        }
    }

    override fun printRecords() {
        report.nodes = physicalLayer.getAll().toMutableList()
        //writeToFile(mapper.writeValueAsString(report))

        driver.session().apply {
            writeTransaction { tx ->
                val id = numberOfEvents++
                val stats = report.aggregation
                tx.run(
                        "MATCH (sim:Simulation {simId: \$simId}) " +
                        "CREATE (n:Stats {value: \$value}) " +
                        "CREATE (sim)-[:HAS]->(n)",
                        mapOf("value" to mapper.writeValueAsString(stats), "id" to id, "simId" to simulationId)
                )
            }
        }

        callbacks?.simulationFinished(simulationId)
    }

    private fun getStats(id: String): Statistics {
        var stats = report.aggregation.stats.firstOrNull { it.node == id }

        if (stats == null) {
            stats = Statistics(id)
            report.aggregation.stats.add(stats)
        }

        return stats
    }

    private fun saveSnapshot(snapshot: SimulationSnapshot) {
        driver.session().apply {
            writeTransaction { tx ->
                val id = numberOfEvents

                tx.run(
                        "MATCH (sim:Simulation {simId: \$simId}) " +
                                "CREATE (n:Snapshot) SET n.id = \$id " +
                                "CREATE (sim)-[:CONTAINS]->(n)",
                        mapOf("simId" to simulationId, "id" to id)
                )

                snapshot.nodes.forEach { node ->
                    tx.run(
                            "MATCH (s:Simulation {simId: \$simId}) " +
                                    "MERGE (n:Node {id: \$nodeId, name: \$nodeName}) " +
                                    "MERGE (s)-[:CONTAINS]->(n)",
                            mapOf("simId" to simulationId, "nodeId" to node.id, "nodeName" to node.device.name)
                    )

                    val connections = mutableListOf<String>()
                    snapshot.connections.forEach { conn ->
                        if (node.id in conn) {
                            val other = if (conn.first == node.id) {
                                conn.second
                            } else {
                                conn.first
                            }
                            connections.add(other)
                        }
                    }

                    tx.run("MATCH (sim:Simulation)-->(snap:Snapshot), (sim)-->(n:Node) " +
                            "WHERE sim.simId = \$simId AND snap.id = \$id AND n.id = \$nodeId " +
                            "CREATE (snap)-[r:CONTAINS]->(n) " +
                            "SET r.x = ${node.position.x}, r.y = ${node.position.y}, r.conn = \$conn", mapOf(
                                "simId" to simulationId,
                                "id" to id,
                                "nodeId" to node.id,
                                "conn" to mapper.writeValueAsString(connections)
                            ))
                }

                val event = snapshot.event
                val name = event.name.replace("\\s".toRegex(), "").split("-").first()
                tx.run(
                        "MATCH (:Simulation {simId: \$simId})-->(snap:Snapshot {id: \$id}) " +
                                "CREATE (e:$name:Event {time: \$time, args: \$args}) " +
                                "CREATE (snap)-[:HAS]->(e)",
                        mapOf("simId" to simulationId, "id" to id, "time" to event.time, "args" to mapper.writeValueAsString(event.args))
                )

                val stats = snapshot.aggregation
                tx.run(
                        "MATCH (:Simulation {simId: \$simId})-->(snap:Snapshot {id: \$id}) " +
                                "CREATE (stats:Stats {value: \$value}) " +
                                "CREATE (snap)-[:HAS]->(stats)",
                        mapOf("simId" to simulationId, "id" to id, "value" to mapper.writeValueAsString(stats))
                )
            }
        }
    }
}