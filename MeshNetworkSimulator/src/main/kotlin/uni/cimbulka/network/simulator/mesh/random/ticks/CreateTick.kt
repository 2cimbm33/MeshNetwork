package uni.cimbulka.network.simulator.mesh.random.ticks

import com.sun.javafx.geom.Vec2d
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.mesh.NetworkNode

class CreateTick(node: NetworkNode,
                 val initPosition: Position,
                 val initVector: Vec2d = Vec2d()) : RandomTick(node, Types.CREATE_NODE)