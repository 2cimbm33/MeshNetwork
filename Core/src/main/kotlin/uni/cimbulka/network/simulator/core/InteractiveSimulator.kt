package uni.cimbulka.network.simulator.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uni.cimbulka.network.simulator.core.events.ShutdownEvent
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.interfaces.SimulationCallbacks
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

open class InteractiveSimulator(private val callbacks: SimulationCallbacks) : AbstractSimulator() {
    override val events = ListQueue<EventInterface>()

    override fun start() {
        CoroutineScope(EmptyCoroutineContext).launch {
            val startTime = Date().time

            fun setTime() {
                time = (Date().time - startTime).toDouble()
                launch { callbacks.updateTime(time) }
            }

            main@ while (true) {
                while (true) {
                    val event = events.peek()
                    if (event == null || event.time > time) break
                    if (event is ShutdownEvent) break@main

                    event(this@InteractiveSimulator)
                    launch { callbacks.executed(event, time) }
                    events.remove(event)
                    setTime()
                }

                delay(1)
                setTime()
            }

            callbacks.stopped()
        }

    }

    override fun stop() {
        events.removeAll()
        events.insert(ShutdownEvent(time))
    }
}

fun main() {
    val simulator = InteractiveSimulator(object : SimulationCallbacks {
        override fun updateTime(time: Double) {
            println("Time: $time")
        }

        override fun executed(event: EventInterface, executedAt: Double) {
            println("Just ran ${event.name} at $executedAt should be ${event.time}")
        }

        override fun stopped() {
            println("Simulator stopped")
        }
    })

    simulator.insert(0.0, "First event") {
        print("Executing first event. ")
        println("And scheduling new one")

        it.insert(100.0, "Sub-event of first event") { _ ->
            println("Execution sub-event of first event")
        }
    }

    simulator.insert(1000.0, "Second event") {
        println("Executing second event")
    }

    simulator.start()

    Thread.sleep(500)
    simulator.insert(simulator.time, "Third event") {
        println("Executing third event, which should execute before second event")
    }

    Thread.sleep(1000)
    simulator.stop()

    Thread.sleep(5000)
}