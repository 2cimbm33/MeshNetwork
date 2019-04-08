package uni.cimbulka.network.simulator.core.models

import uni.cimbulka.network.simulator.core.interfaces.EventInterface

abstract class AbstractSimulator {
    protected abstract val events: OrderedSet<EventInterface>
    val numberOfEvents: Int
        get() = events.size

    var time: Double = 0.0
        protected set

    abstract fun start()

    @Synchronized
    open fun insert(event: EventInterface) {
        events.insert(event)
    }

    @Synchronized
    open fun insert(time: Double, name: String, block: (AbstractSimulator) -> Unit) {
        events.insert(event(time, name, block))
    }

    open fun cancel(event: EventInterface): EventInterface? {
        return events.remove(event)
    }

    open fun stop() {
        events.removeAll()
    }
}