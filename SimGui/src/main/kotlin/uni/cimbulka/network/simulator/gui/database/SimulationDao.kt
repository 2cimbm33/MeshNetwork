package uni.cimbulka.network.simulator.gui.database

import javafx.beans.property.ReadOnlyProperty
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

    fun getSnapshots() {
        runAsync {
            val result = mutableListOf<String>()

            driver.session().run {
                readTransaction { tx ->
                    val rs = tx.run("MATCH (sim:Simulation)-->(snap:Snapshot) " +
                            "WHERE sim.simId = \$simId " +
                            "RETURN snap " +
                            "ORDER BY snap.id", mapOf("simId" to mainController.simId))

                    for (record in rs) {
                        val node = record["snap"].asNode() ?: continue
                        val snapId = node["id"].asInt()
                        val snapName = node["name"].asString()
                        result.add("$snapId $snapName")
                    }
                }
            }

            result
        } ui {
            snapshotList.clear()
            snapshotList.addAll(it)
        }
    }

    fun getSimNodes() {
        runAsync {
            val result = mutableListOf<Node>()

            driver.session().run {
                readTransaction { tx ->
                    val rs = tx.run("MATCH (s:Simulation)-->(n:Node) " +
                            "WHERE s.simId = \$simId " +
                            "RETURN n", mapOf("simId" to mainController.simId))

                    for (record in rs) {
                        val node = record["n"].asNode() ?: continue
                        val nodeId = node["id"].asString() ?: continue
                        val nodeName = node["name"].asString() ?: continue

                        result.add(Node(nodeId, nodeName))
                    }
                }
            }
            result
        } ui {
            simNodeList.clear()
            simNodeList.addAll(it)
        }
    }
}