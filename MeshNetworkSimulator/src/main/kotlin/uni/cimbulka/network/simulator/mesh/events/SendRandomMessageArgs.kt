package uni.cimbulka.network.simulator.mesh.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.mesh.NetworkNode

data class SendRandomMessageArgs(val sender: NetworkNode, val recipient: NetworkNode) : EventArgs()