package uni.cimbulka.network.simulator.mesh

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface
import uni.cimbulka.network.simulator.core.models.Event
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
            val reportEvent = ReportEvent(event, physicalLayer.getAll())
            val json = mapper.valueToTree<JsonNode>(reportEvent)

            report.events["[$numberOfEvents] [${event.time}] ${event.name}"] = json
            //println("\n[$numberOfEvents] [${event.time}]:\n${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)}\n")

            numberOfEvents++
        }
    }

    override fun printRecords() {
        //println("\n\nMonitor report starting:\n")
        //val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report)
        //println(json)

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
}