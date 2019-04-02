package uni.cimbulka.network.simulator.core.models

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.interfaces.Comparable
import uni.cimbulka.network.simulator.core.interfaces.EventInterface

abstract class Event<T : EventArgs>(override val name: String, val args: T) : EventInterface {
    override operator fun compareTo(other: Comparable): Int {
        return when(other) {
            is Event<*> -> time.compareTo(other.time)
            else -> throw IllegalArgumentException("Cannot compare ${this.javaClass.name} to ${other.javaClass.name}")
        }
    }
}

fun event(time: Double, name: String, block: (AbstractSimulator) -> Unit): Event<EventArgs> {
    return object : Event<EventArgs>(name, EventArgs.empty) {
        override val time = time

        override fun invoke(simulator: AbstractSimulator) {
            block(simulator)
        }
    }
}