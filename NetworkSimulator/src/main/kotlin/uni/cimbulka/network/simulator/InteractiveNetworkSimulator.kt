package uni.cimbulka.network.simulator

import uni.cimbulka.network.simulator.core.InteractiveSimulator
import uni.cimbulka.network.simulator.core.interfaces.SimulationCallbacks

abstract class InteractiveNetworkSimulator(callbacks: SimulationCallbacks) : InteractiveSimulator(callbacks) {

    init {
        Session.simulator = this
    }

    abstract fun run()
}