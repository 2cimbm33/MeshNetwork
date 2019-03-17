package uni.cimbulka.network.simulator.core

import uni.cimbulka.network.simulator.core.models.EmptyEventArgs

abstract class EventArgs {
    companion object {
        val empty: EventArgs
            get() = EmptyEventArgs()
    }
}