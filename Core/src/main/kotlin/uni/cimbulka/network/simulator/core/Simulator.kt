package uni.cimbulka.network.simulator.core

import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.DefaultMonitor
import uni.cimbulka.network.simulator.core.models.Event

open class Simulator(open val monitor: MonitorInterface = DefaultMonitor()) : AbstractSimulator() {
    override val events = ListQueue<EventInterface>()

    override fun start() {
        println("[$time] Starting simulation")

        while (true) {
            (events.removeFirst() as Event<*>?)?.let {
                time = it.time
                it(this)
                monitor.record(it)
            } ?: break
        }

        println("[$time] Simulation ended\n")
        monitor.printRecords()
    }
}