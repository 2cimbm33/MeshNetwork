package uni.cimbulka.network.simulator.core.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import kotlin.concurrent.withLock

data class TimerEventArgs(val interval: Double, var invocations: Int = Int.MAX_VALUE, val block: (Double) -> Unit) : EventArgs()

class TimerEvent(override val time: Double, args: TimerEventArgs) : Event<TimerEventArgs>("Timer", args) {
    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val (interval, invocations, block) = args
            if (invocations != -1) args.invocations--
            block(time)

            if (invocations > 0 || invocations == -1)
                simulator.insert(TimerEvent(time + interval, args))
        }
    }
}