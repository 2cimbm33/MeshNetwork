package uni.cimbulka.network.simulator.gui

import com.fasterxml.jackson.databind.ObjectMapper
import uni.cimbulka.network.simulator.gui.models.Report
import java.io.File

object FileLoader {
    fun readFile(fileName: String): String {
        return File(fileName).readText()
    }
}

fun main() {
    val json = FileLoader.readFile("simulationReport.json")
    val report = ObjectMapper().readValue(json, Report::class.java)

    println(report.events.size)
    println(report)
}