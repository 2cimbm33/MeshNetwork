package uni.cimbulka.network.simulator.core.models

import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.MonitorInterface

class DefaultMonitor : MonitorInterface {
    private val records = mutableMapOf<String, Int>()

    override fun record(event: EventInterface) {
        val type = event.javaClass.name
        records[type] = records[type]?.inc() ?: 1
    }

    override fun printRecords() {
        println("Printing monitor records:")
        records.forEach { type, number ->
            println("$type was invoked $number times")
        }
    }
}