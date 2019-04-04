package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.neo4j.driver.v1.Driver
import uni.cimbulka.network.packets.*
import uni.cimbulka.network.simulator.bluetooth.events.EndDiscoveryEvent
import uni.cimbulka.network.simulator.bluetooth.events.ReceivePacketEvent
import uni.cimbulka.network.simulator.bluetooth.events.SendPacketEvent
import uni.cimbulka.network.simulator.bluetooth.events.StartDiscoveryEvent
import uni.cimbulka.network.simulator.common.Node
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.mesh.events.StartNodeEvent
import uni.cimbulka.network.simulator.mesh.reporting.Report
import uni.cimbulka.network.simulator.mesh.reporting.SimulationSnapshot
import uni.cimbulka.network.simulator.mesh.reporting.Statistics
import uni.cimbulka.network.simulator.mobility.events.RunMobilityEvent
import uni.cimbulka.network.simulator.physical.PhysicalLayer
import uni.cimbulka.network.simulator.physical.events.AddNodeEvent
import uni.cimbulka.network.simulator.physical.events.MoveNodeEvent
import uni.cimbulka.network.simulator.physical.events.RemoveNodeEvent

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

    internal var callbacks: BaseSimulationCallbacks? = null

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
                val event = snapshot.event
                val name = event.name.replace("\\s".toRegex(), "").split("-").first()

                tx.run(
                        "MATCH (sim:Simulation {simId: \$simId}) " +
                                "CREATE (n:Snapshot) SET n.id = \$id, n.name = \$name " +
                                "CREATE (sim)-[:CONTAINS]->(n)",
                        mapOf("simId" to simulationId, "id" to id, "name" to name)
                )

                snapshot.nodes.forEach { node ->
                    tx.run(
                            "MATCH (s:Simulation {simId: \$simId}) " +
                                    "MERGE (n:Node {id: \$nodeId, name: \$nodeName}) " +
                                    "MERGE (s)-[:CONTAINS]->(n)",
                            mapOf("simId" to simulationId, "nodeId" to node.id, "nodeName" to node.device.name)
                    )

                    val connections = getConnections(snapshot, node)

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

                getMainNodeId(event)?.let{ mainNodeId ->
                    physicalLayer[mainNodeId]?.let {
                        val connections = getConnections(snapshot, it)
                        val inRange = getInRange(it)

                        tx.run("MATCH (sim:Simulation)-->(snap:Snapshot), (sim)-->(n:Node) " +
                                "WHERE sim.simId = \$simId AND snap.id = \$id AND n.id = \$nodeId " +
                                "CREATE (snap)-[r:MAIN]->(n) " +
                                "SET r.x = ${it.position.x}, r.y = ${it.position.y}, r.conn = \$conn, r.range = \$range", mapOf(
                                "simId" to simulationId,
                                "id" to id,
                                "nodeId" to it.id,
                                "conn" to mapper.writeValueAsString(connections),
                                "range" to mapper.writeValueAsString(inRange)
                        ))
                    }
                }

                tx.run(
                        "MATCH (:Simulation {simId: \$simId})-->(snap:Snapshot {id: \$id}) " +
                                "CREATE (e:$name:Event {time: \$time, args: \$args, name: \$name}) " +
                                "CREATE (snap)-[:HAS]->(e)",
                        mapOf(
                                "simId" to simulationId,
                                "id" to id,
                                "time" to event.time,
                                "args" to mapper.writeValueAsString(event.args),
                                "name" to name
                        )
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

    private fun getMainNodeId(event: EventInterface): String? {
        return when (event) {
            is AddNodeEvent -> event.args.node.id
            is MoveNodeEvent -> event.args.id
            is RemoveNodeEvent -> event.args.node.id
            is RunMobilityEvent -> event.args.rule.node
            is StartDiscoveryEvent -> event.args.adapter.node.id
            is EndDiscoveryEvent -> event.args.adapter.node.id
            is SendPacketEvent -> event.args.adapter.node.id
            is ReceivePacketEvent -> event.args.adapter.node.id
            is StartNodeEvent -> event.args.node.id

            else -> null
        }
    }

    private fun getConnections(snapshot: SimulationSnapshot, node: Node): List<String> {
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
        return connections.toList()
    }

    private fun getInRange(node: Node): List<String> {
        val inRange = mutableListOf<String>()

        physicalLayer.keys.filter { it != node.id }.forEach {
            if (physicalLayer.inRange(node.id, it)) {
                inRange.add(it)
            }
        }

        return inRange
    }
}