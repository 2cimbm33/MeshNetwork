package uni.cimbulka.network.events

import uni.cimbulka.network.NetworkSession
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.core.models.AbstractSimulator
import uni.cimbulka.network.simulator.core.models.Event

data class StartDiscoveryEventArgs(val session: NetworkSession) : EventArgs()

internal class StartDiscoveryEvent(override val time: Double, args: StartDiscoveryEventArgs) :
        Event<StartDiscoveryEventArgs>("MeshStartDiscovery", args) {

    override fun invoke(simulator: AbstractSimulator) {
        args.session.services.forEach { it.startDiscovery(args.session.isInNetwork) }
    }
}