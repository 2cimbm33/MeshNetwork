package uni.cimbulka.network.simulator.mesh.events

import com.fasterxml.jackson.annotation.JsonIgnore
import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.mesh.NetworkNode
import uni.cimbulka.network.simulator.physical.PhysicalLayer

data class StartNodeEventArgs(val node: NetworkNode,
                              @JsonIgnore val physicalLayer: PhysicalLayer) : EventArgs()