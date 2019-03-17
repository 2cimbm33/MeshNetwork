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

    override fun record(event: EventInterface) {
        if (event is Event<*>) {
            val json: JsonNode = mapper.valueToTree(event)

            report.events["[${event.time}] ${event.name}"] = json
            println("\n[${event.time}]:\n${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)}\n")
        }

    }

    override fun printRecords() {
        println("\n\nMonitor report starting:\n")
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report)
        println(json)

        writeToFile(json)
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