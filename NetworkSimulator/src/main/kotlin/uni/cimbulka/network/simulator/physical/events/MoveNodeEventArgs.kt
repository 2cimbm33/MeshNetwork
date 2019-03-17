package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.physical.PhysicalLayer

data class MoveNodeEventArgs(val id: String,
                             val dx: Double,
                             val dy: Double,
                             val physicalLayer: PhysicalLayer) : EventArgs()