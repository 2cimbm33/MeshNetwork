package uni.cimbulka.network.simulator.gui.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.scene.control.Alert
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property
import uni.cimbulka.network.simulator.gui.database.Database
import uni.cimbulka.network.simulator.gui.database.SimulationDao
import uni.cimbulka.network.simulator.gui.database.SnapshotDao
import uni.cimbulka.network.simulator.gui.models.Report
import uni.cimbulka.network.simulator.gui.views.MainView
import uni.cimbulka.network.simulator.gui.views.SimulationsView
import uni.cimbulka.network.simulator.mesh.*

class MainController : Controller() {
    private val simDao: SimulationDao by inject()
    private val snapDao: SnapshotDao by inject()

    private var simulationRunning = false

    val eventList: ObservableList<Int>
        get () = simDao.snapshotList

    var simId: String? by property("")
    fun simIdProperty() = getProperty(MainController::simId)

    var report: Report? by property()
    fun reportProperty() = getProperty(MainController::report)

    var nodes: String by property("")
    fun nodesProperty() = getProperty(MainController::nodes)

    val stats: String
        get() = simDao.simStats
    fun statsProperty() = simDao.simStatsProperty()

    init {
        simIdProperty().onChange {
            println(it)
            //openFile("simulationReport.json")
            simDao.getSnapshots()
            simDao.getSimNodes()
            simDao.getSimStats()
        }

        simDao.simNodeList.onChange {
            val mapper = ObjectMapper()
            nodes = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(simDao.simNodeList)
        }
    }

    fun openSimulationPicker() {
        if (!simulationRunning)
            find<SimulationsView>().openModal(escapeClosesWindow = true, block = true)
    }

    fun handleEventListClicked(item: Int?) {
        item?.let {
            println(it)
            snapDao.getSnapshot(it)
        }
    }

    private val alert = Alert(Alert.AlertType.INFORMATION).apply {
        title = "Simulation in progress"
        headerText = null
        contentText = "Simulation is currently in progress. Pleas wait until it finishes."

        setOnCloseRequest {
            if (simulationRunning)
                it.consume()
        }
    }

    fun runSimulation(type: String) {
        val sim = getSimulation(type) ?: return
        simulationRunning = true

        runAsync {
            sim.run()
        }

        alert.show()
    }

    private fun getSimulation(type: String): BaseSimulation? {
        val result = when (type) {
            "Simulation1" -> Simulation1(Database.driver)
            "Simulation2" -> Simulation2(Database.driver)
            "Simulation3" -> Simulation3(Database.driver)
            "Simulation4" -> Simulation4(Database.driver)
            else -> null
        } ?: return null

        result.simulationCallbacks = object : SimulationCallbacks {
            override fun simulationFinished(id: String) {
                Platform.runLater {
                    simulationRunning = false
                    alert.close()
                    simId = id
                }
            }
        }

        return result
    }
}