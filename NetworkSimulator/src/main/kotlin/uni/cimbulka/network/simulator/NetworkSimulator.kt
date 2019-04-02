package uni.cimbulka.network.simulator

import uni.cimbulka.network.simulator.core.Simulator
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface

abstract class NetworkSimulator(monitor: MonitorInterface) : Simulator(monitor) {

    init {
        Session.simulator = this
    }

    abstract fun run()
}