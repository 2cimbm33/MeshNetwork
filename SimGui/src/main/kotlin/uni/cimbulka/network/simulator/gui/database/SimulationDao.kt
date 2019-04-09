package uni.cimbulka.network.simulator.gui.database

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.MainController
import uni.cimbulka.network.simulator.gui.models.Node
import uni.cimbulka.network.simulator.gui.models.Simulation

typealias NeoNode = org.neo4j.driver.v1.types.Node

class SimulationDao : Controller() {
    private val mainController: MainController by inject()
    private val driver = Database.driver

    val simulationsList: ObservableList<Simulation> = FXCollections.observableArrayList()
    val snapshotList: ObservableList<String> = FXCollections.observableArrayList()
    val simNodeList: ObservableList<Node> = FXCollections.observableArrayList()

    fun getSimulations() {
        runAsync {
            val result = mutableListOf<Simulation>()

            driver.session().run {
                val rs = run("MATCH (s:Simulation) RETURN s")
                rs.forEach { record ->
                    if (record.containsKey("s")) {
                        val node = record["s"].asNode()
                        if (node.containsKey("simId")) {
                            result.add(Simulation(node["simId"].asString(), getSimName(node)))
                        }
                    }
                }
            }

            result.toList()
        } ui {
            simulationsList.clear()
            simulationsList.addAll(it)
        }
    }

    private fun getSimName(simulation: NeoNode): String {
        for (label in simulation.labels()) {
            if (label != "Simulation") {
                return label
            }
        }

        return ""
    }

}