package uni.cimbulka.network.simulator.mobility.events

import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event
import uni.cimbulka.network.simulator.mobility.MobilityManager

class AddNewMobilityRuleEvent(override val time: Double, args: MobilityEventArgs) :
        Event<MobilityEventArgs>("AddNewMobilityRule", args) {

    override fun invoke(simulator: AbstractSimulator) {
        val ( rule ) = args

        MobilityManager.addRule(rule.node, rule)
        simulator.insert(RunMobilityEvent(time, args))
    }

}