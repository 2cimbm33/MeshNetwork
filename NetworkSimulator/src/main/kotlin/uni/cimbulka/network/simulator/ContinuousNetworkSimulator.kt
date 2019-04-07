package uni.cimbulka.network.simulator

import uni.cimbulka.network.simulator.core.ContinuousSimulator
import uni.cimbulka.network.simulator.core.interfaces.SimulationCallbacks

abstract class ContinuousNetworkSimulator(callbacks: SimulationCallbacks) : ContinuousSimulator(callbacks) {

    init {
        Session.simulator = this
    }

    abstract fun run()
}