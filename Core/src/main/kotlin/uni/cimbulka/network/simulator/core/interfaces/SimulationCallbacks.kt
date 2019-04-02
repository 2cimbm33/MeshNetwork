package uni.cimbulka.network.simulator.core.interfaces

interface SimulationCallbacks {
    fun updateTime(time: Double)
    fun executed(event: EventInterface, executedAt: Double)
    fun stopped()
}