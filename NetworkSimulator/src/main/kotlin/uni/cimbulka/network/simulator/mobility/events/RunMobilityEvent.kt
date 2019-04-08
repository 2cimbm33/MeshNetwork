package uni.cimbulka.network.simulator.mobility.events

import uni.cimbulka.network.simulator.core.events.TimerEvent
import uni.cimbulka.network.simulator.core.events.TimerEventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.mobility.MobilityRule
import uni.cimbulka.network.simulator.physical.events.MoveNodeEvent
import uni.cimbulka.network.simulator.physical.events.MoveNodeEventArgs
import kotlin.concurrent.withLock
import kotlin.random.Random

class RunMobilityEvent(override val time: Double, args: MobilityEventArgs) :
        Event<MobilityEventArgs>("RunMobility", args) {

    override fun invoke(simulator: AbstractSimulator) {
        lock.withLock {
            val rule = args.rule
            val step = rule.speed / 10.0
            val iterations = rule.distance / step
            val timerEventArgs = TimerEventArgs(100.0, iterations.toInt() - 1) {
                var dx = 0.0
                var dy = 0.0

                val direction = if (rule.direction == MobilityRule.Direction.RANDOM) {
                    MobilityRule.Direction.values()[Random.nextInt(0, 3)]
                } else {
                    rule.direction
                }

                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (direction) {
                    MobilityRule.Direction.LEFT -> dx -= step
                    MobilityRule.Direction.RIGHT -> dx += step
                    MobilityRule.Direction.DOWN -> dy -= step
                    MobilityRule.Direction.UP -> dy += step
                }

                simulator.insert(MoveNodeEvent(it, MoveNodeEventArgs(
                        rule.node, dx, dy, rule.physicalLayer
                )))
            }
            simulator.insert(TimerEvent(time, timerEventArgs))
        }
    }
}