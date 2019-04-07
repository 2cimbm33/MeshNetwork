package uni.cimbulka.network.simulator.gui.views

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TabPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.SnapshotController

class SnapshotView : View() {
    private val controller: SnapshotController by inject()
    private val graphView: GraphView by inject()

    override val root = borderpane {
        top = hbox {
            alignment = Pos.CENTER
            padding = Insets(10.0)
            spacing = 20.0

            hbox {
                spacing = 5.0

                label("Name:") {
                    style = "-fx-font-weight: bold"
                }
                label {
                    bind(controller.nameProperty())
                }
            }

            hbox {
                spacing = 5.0

                label("Time:") {
                    style = "-fx-font-weight: bold"
                }
                label {
                    bind(controller.timeProperty())
                }
            }
        }

        center = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tab("Args") {
                scrollpane {
                    padding = Insets(10.0)

                    label {
                        bind(controller.argsProperty())
                    }
                }
            }

            tab("Nodes") {
                scrollpane {
                    padding = Insets(10.0)

                    vbox {
                        label("Nodes:")
                        label {
                            bind(controller.nodesProperty())
                        }

                        label("Connections")
                        label {
                            bind(controller.connectionsProperty())
                        }
                    }

                }
            }

            tab("Stats") {
                scrollpane {
                    padding = Insets(10.0)

                    label {
                        bind(controller.statsProperty())
                    }
                }
            }

            tab("Graph") {
                graphView.fireEvents = false
                add(graphView)
            }
        }
    }
}