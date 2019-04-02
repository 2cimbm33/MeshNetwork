package uni.cimbulka.network.simulator.gui.models

import uni.cimbulka.network.simulator.common.Position

data class AddNodeDialogResult(val name: String, val position: Position, val delay: Double = 0.0)