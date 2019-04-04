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

                    launch {
                        event(this@InteractiveSimulator)
                        callbacks.executed(event, time)
                    }
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