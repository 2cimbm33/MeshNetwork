package uni.cimbulka.network.simulator.gui.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.onChange
import tornadofx.property
import uni.cimbulka.network.simulator.gui.database.Database
import uni.cimbulka.network.simulator.gui.database.SimulationDao
import uni.cimbulka.network.simulator.gui.database.SnapshotDao
import uni.cimbulka.network.simulator.gui.models.Report
import uni.cimbulka.network.simulator.gui.views.SimulationsView
import uni.cimbulka.network.simulator.mesh.Simulation1
import uni.cimbulka.network.simulator.mesh.Simulation2
import uni.cimbulka.network.simulator.mesh.Simulation3
import uni.cimbulka.network.simulator.mesh.Simulation4

class MainController : Controller() {
    private val simDao: SimulationDao by inject()
    private val snapDao: SnapshotDao by inject()

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
        find<SimulationsView>().openModal(escapeClosesWindow = true, block = true)
    }

    fun handleEventListClicked(item: Int?) {
        item?.let {
            println(it)
            snapDao.getSnapshot(it)
        }
    }

    fun runSimulation(type: String) {
        runAsync {
            when (type) {
                "Simulation1" -> Simulation1(Database.driver).run()
                "Simulation2" -> Simulation2(Database.driver).run()
                "Simulation3" -> Simulation3(Database.driver).run()
                "Simulation4" -> Simulation4(Database.driver).run()
            }
        }
    }
}