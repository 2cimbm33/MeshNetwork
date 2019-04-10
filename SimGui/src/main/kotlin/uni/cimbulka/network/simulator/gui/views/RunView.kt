package uni.cimbulka.network.simulator.gui.views

import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.RunController

class RunView : View("Run View") {
    private val controller: RunController by inject()
    private val graphView: GraphView by inject()

    override val root = vbox {
        hbox {
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

        hbox {
            label("Current number of nodes: ")
            label {
                controller.numberOfNodesProperty.onChange {
                    text = it.toString()
                }
            }
        }
        hbox {
            label("Total number of events: ")
            label {
                controller.numberOfEventProperty.onChange {
                    text = it.toString()
                }
            }
        }

        hbox {
            label("Avg time per event: ")
            label {
                controller.eventTimeProperty.onChange {
                    text = it.toString()
                }
            }
        }

        button("Open graph view") {
            action {
                graphView.openWindow()
            }
        }
    }

    override fun onDock() {
        graphView.openWindow()
        graphView.fireEvents = false
    }
}