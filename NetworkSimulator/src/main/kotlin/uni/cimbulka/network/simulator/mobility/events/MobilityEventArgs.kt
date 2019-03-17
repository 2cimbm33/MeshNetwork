package uni.cimbulka.network.simulator.mobility.events

import uni.cimbulka.network.simulator.core.EventArgs
import uni.cimbulka.network.simulator.mobility.MobilityRule

data class MobilityEventArgs(val rule: MobilityRule) : EventArgs()