package uni.cimbulka.network.simulator.gui

import javafx.application.Platform
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.SimulationCallbacks
import uni.cimbulka.network.simulator.gui.controllers.InteractiveSimulationController

class SimulationCallbacksImpl(private val controller: InteractiveSimulationController) : SimulationCallbacks {
    override fun updateTime(time: Double) {
        Platform.runLater {
            controller.time = time
        }
    }


    override fun executed(event: EventInterface, executedAt: Double) {
        Platform.runLater {
            controller.events.add(event)
        }
    }

    override fun stopped() {
        Platform.runLater {
            controller.running = false
        }
    }
}