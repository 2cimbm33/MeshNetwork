package uni.cimbulka.network.simulator.core.interfaces

import uni.cimbulka.network.simulator.core.models.AbstractSimulator

interface EventInterface : Comparable {
    val time: Double
    val name: String

    operator fun invoke(simulator: AbstractSimulator)
    override operator fun compareTo(other: Comparable): Int
}