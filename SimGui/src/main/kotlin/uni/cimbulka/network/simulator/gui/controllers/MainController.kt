package uni.cimbulka.network.simulator.gui.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import uni.cimbulka.network.simulator.gui.FileLoader
import uni.cimbulka.network.simulator.gui.models.Report
import uni.cimbulka.network.simulator.gui.views.SimulationsView

class MainController : Controller() {
    val snapshotController: SnapshotController by inject()
    private val simulationsView: SimulationsView by inject()

    val eventList: ObservableList<String> = FXCollections.observableArrayList()

    var simId: String? by property("")
    fun simIdProperty() = getProperty(MainController::simId)

    var report: Report? by property()
    fun reportProperty() = getProperty(MainController::report)

    var nodes: String by property("")
    fun nodesProperty() = getProperty(MainController::nodes)

    var stats: String by property("")
    fun statsProperty() = getProperty(MainController::stats)

    init {
        simIdProperty().onChange {
            println(it)
            openFile("simulationReport.json")
        }
    }

    fun openSimulationPicker() {
        simulationsView.openModal(escapeClosesWindow = true, block = true)
    }

    fun handleEventListClicked(item: String?) {
        item?.let {
            if (report.toProperty().get() != null) {
                val snapshot = report?.events?.get(item) ?: return
                snapshotController.display(snapshot)
            }
        }
    }

    private fun openFile(fileName: String) {
        runAsync {
            //val simulator = Simulation1()
            //simulator.run()

            Report.fromJson(FileLoader.readFile(fileName))
        } ui { report ->
            this.report = report
            eventList.addAll(report.events.keys)

            val mapper = ObjectMapper()
            val builder = StringBuilder()

            report.nodes.forEach {
                builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
                builder.appendln()
            }
            nodes = builder.toString()

            builder.clear()
            report.aggregation.stats.forEach {
                builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
                builder.appendln()
            }
            stats = builder.toString()
        }
    }
}