package uni.cimbulka.network.simulator.mesh.random.ticks

import com.sun.javafx.geom.Vec2d
import uni.cimbulka.network.simulator.mesh.NetworkNode

class MoveTick(val node: NetworkNode, val vector: Vec2d) : RandomTick(Types.MOVE_NODE)