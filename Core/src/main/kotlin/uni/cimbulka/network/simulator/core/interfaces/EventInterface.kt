package uni.cimbulka.network.simulator.core.interfaces

import uni.cimbulka.network.simulator.core.models.AbstractSimulator

interface EventInterface : Comparable {
    operator fun invoke(simulator: AbstractSimulator)
    override operator fun compareTo(other: Comparable): Int
}