package uni.cimbulka.network.simulator.core.interfaces

interface MonitorInterface {
    fun record(event: EventInterface)
    fun printRecords()
}