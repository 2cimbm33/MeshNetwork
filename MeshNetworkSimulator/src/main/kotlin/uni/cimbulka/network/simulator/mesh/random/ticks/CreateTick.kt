package uni.cimbulka.network.simulator.mesh.random.ticks

import com.sun.javafx.geom.Vec2d
import uni.cimbulka.network.simulator.common.Position

class CreateTick(val initPosition: Position,
                 val initVector: Vec2d = Vec2d()) : RandomTick(Types.CREATE_NODE)