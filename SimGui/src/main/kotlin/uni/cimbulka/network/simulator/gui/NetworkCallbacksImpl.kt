package uni.cimbulka.network.simulator.gui

import javafx.application.Platform
import uni.cimbulka.network.data.ApplicationData
import uni.cimbulka.network.listeners.NetworkCallbacks
import uni.cimbulka.network.simulator.gui.controllers.InteractiveSimulationController

class NetworkCallbacksImpl(private val controller: InteractiveSimulationController) : NetworkCallbacks {

    lateinit var deviceName: String

    override fun onDataReceived(data: ApplicationData) {
        Platform.runLater {
            controller.onDataReceived(data, deviceName)
        }
    }
}