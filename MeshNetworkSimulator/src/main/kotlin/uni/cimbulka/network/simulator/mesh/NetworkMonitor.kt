package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
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
import java.io.File

class NetworkMonitor(val physicalLayer: PhysicalLayer) : MonitorInterface {
    private val report = Report()
    private val mapper = ObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    private var numberOfEvents = 0

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
                    }

                    stats.totalPacketsReceived++
                }
            }

            val snapshot = SimulationSnapshot(event, report.aggregation, physicalLayer)
            val json = mapper.valueToTree<JsonNode>(snapshot)

            report.events["[$numberOfEvents] [${event.time}] ${event.name}"] = json
            //println("\n[$numberOfEvents] [${event.time}]:\n${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)}\n")

            numberOfEvents++
        }
    }

    override fun printRecords() {
        //println("\n\nMonitor report starting:\n")
        //val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report)
        //println(json)

        report.nodes = physicalLayer.getAll().toMutableList()
        writeToFile(mapper.writeValueAsString(report))
    }

    private fun writeToFile(json: String) {
        val fileName = "simulationReport.json"

        println("\nWriting to file")
        println("File name: $fileName")

        File(fileName).apply {
            println(absolutePath)
            writeText(json)
        }

        println("Done")
    }

    private fun getStats(id: String): Statistics {
        var stats = report.aggregation.stats.firstOrNull { it.node == id }

        if (stats == null) {
            stats = Statistics(id)
            report.aggregation.stats.add(stats)
        }

        return stats
    }
}