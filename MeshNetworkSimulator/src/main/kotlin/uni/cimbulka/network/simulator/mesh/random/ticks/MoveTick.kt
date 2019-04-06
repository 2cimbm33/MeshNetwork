package uni.cimbulka.network.simulator.mesh.random.ticks

import com.sun.javafx.geom.Vec2d
import uni.cimbulka.network.simulator.mesh.NetworkNode

class MoveTick(node: NetworkNode, val vector: Vec2d) : RandomTick(node, Types.MOVE_NODE)