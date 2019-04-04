package uni.cimbulka.network.simulator.physical.events

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.physical.PhysicalLayer

data class PrintPhyLayerEventArgs(@JsonIgnore val physicalLayer: PhysicalLayer) : EventArgs()