package uni.cimbulka.network.simulator.gui.controllers

import javafx.application.Platform
import javafx.beans.property.ReadOnlyProperty
import tornadofx.*
import uni.cimbulka.network.simulator.gui.views.RunView
import uni.cimbulka.network.simulator.gui.views.StartSimulationView
import uni.cimbulka.network.simulator.gui.views.SwitchViewEvent
import uni.cimbulka.network.simulator.mesh.BaseSimulationCallbacks
import uni.cimbulka.network.simulator.mesh.RandomSimulation
import uni.cimbulka.network.simulator.mesh.RandomSimulationConfiguration
import uni.cimbulka.network.simulator.mesh.reporting.Snapshot

class MainController : Controller() {

    private val times = mutableListOf<Long>()

    var time: Double by property(0.0)
        private set
    fun timeProperty() = getProperty(MainController::time) as ReadOnlyProperty<Double>

    var numberOfNodes: Int by property(0)
        private set
    fun numberOfNodesProperty() = getProperty(MainController::numberOfNodes) as ReadOnlyProperty<Int>

    var avgEventTime: Double by property(.0)
        private set
    fun avgEventTimeProperty() = getProperty(MainController::avgEventTime) as ReadOnlyProperty<Double>

    var numberOfEvents: Int by property(0)
        private set
    fun numberOfEventsProperty() = getProperty(MainController::numberOfEvents) as ReadOnlyProperty<Int>

    fun runSimulation(config: RandomSimulationConfiguration) {
        val simulator = RandomSimulation(config)
        simulator.simulationCallbacks = object : BaseSimulationCallbacks {
            override fun eventExecuted(snapshot: Snapshot, time: Long) {
                Platform.runLater {
                    this@MainController.time = snapshot.time
                    this@MainController.times.add(time)
                    this@MainController.numberOfEvents++

                    var total = 0L
                    this@MainController.times.forEach { total += it }
                    this@MainController.avgEventTime =  total.toDouble() / this@MainController.numberOfNodes

                    if (snapshot.eventName == "AddNode") {
                        this@MainController.numberOfNodes++
                    } else if (snapshot.eventName == "RemoveNode") {
                        this@MainController.numberOfNodes--
                    }
                }
            }

            override fun simulationFinished(id: String) {
                Platform.runLater {
                    fire(SwitchViewEvent<StartSimulationView>())
                }
            }

        }

        fire(SwitchViewEvent<RunView>())
        runAsync {
            simulator.run()
        }
    }
}