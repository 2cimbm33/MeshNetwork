package uni.cimbulka.network.simulator.mesh.random.ticks

import uni.cimbulka.network.simulator.mesh.NetworkNode

class SendTick(val sender: NetworkNode, val recipient: NetworkNode) : RandomTick(sender, Types.SEND_MESSAGE)