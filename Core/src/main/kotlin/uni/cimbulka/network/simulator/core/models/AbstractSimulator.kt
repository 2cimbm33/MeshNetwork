package uni.cimbulka.network.simulator.core.models

import uni.cimbulka.network.simulator.core.interfaces.EventInterface

abstract class AbstractSimulator {
    protected abstract val events: OrderedSet<EventInterface>

    var time: Double = 0.0
        protected set

    abstract fun start()

    open fun insert(event: EventInterface) {
        events.insert(event)
    }

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