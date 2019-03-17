package uni.cimbulka.network.simulator.physical.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.physical.PhysicalLayer

data class PrintPhyLayerEventArgs(val physicalLayer: PhysicalLayer) : EventArgs()