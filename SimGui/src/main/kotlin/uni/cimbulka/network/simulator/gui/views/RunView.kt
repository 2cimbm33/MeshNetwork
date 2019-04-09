package uni.cimbulka.network.simulator.gui.views

import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.RunController

class RunView : View("Run View") {
    private val controller: RunController by inject()

    override val root = borderpane {
        top = hbox {
            label("Time: ") {
                style = "-fx-font-weight: bold"
            }
            label {
                controller.timeProperty.onChange {
                    text = it.toString()
                }
            }
            label(" ms")
        }

        left = vbox {
            hbox {
                label("Current number of nodes: ")
                label {
                    controller.numberOfNodesProperty.onChange {
                        text = it.toString()
                    }
                }
            }
            hbox {
                label("Total number of nodes: ")
                label {
                    controller.numberOfEventProperty.onChange {
                        text = it.toString()
                    }
                }
            }
        }

        bottom = hbox {
            label("Avg time per event: ")
            label {
                controller.eventTimeProperty.onChange {
                    text = it.toString()
                }
            }
        }
    }
}