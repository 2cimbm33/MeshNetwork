package uni.cimbulka.network.simulator.gui.views

import javafx.geometry.Insets
import javafx.scene.control.TabPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.MainController

class MainView : View("Main View") {
    private val controller: MainController by inject()
    private val snapshotView: SnapshotView by inject()
    private val interactiveSimulationView: InteractiveSimulationView by inject()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        prefWidth = 1024.0
        prefHeight = 640.0

        tab("Main") {
            borderpane {
                left = vbox {
                    button("Run Simulation1") {
                        action {
                            controller.runSimulation("Simulation1")
                        }
                    }

                    button("Run Simulation2") {
                        action {
                            controller.runSimulation("Simulation2")
                        }
                    }

                    button("Run Simulation3") {
                        action {
                            controller.runSimulation("Simulation3")
                        }
                    }

                    button("Run Simulation4") {
                        action {
                            controller.runSimulation("Simulation4")
                        }
                    }

                    button("Run random simulation") {
                        action {
                            controller.runSimulation("RandomSimulation")
                        }
                    }

                }

                center = vbox {
                    button("Open Simulation") {
                        action(controller::openSimulationPicker)
                    }
                    button("Refresh") {
                        action(controller::refreshSimulation)
                    }
                }
            }
        }

        tab("Interactive Simulation") {
            add(interactiveSimulationView)
        }

        tab("Events") {
            borderpane {
                center = snapshotView.root

                left = listview<String> {
                    prefWidth = 400.0
                    items = controller.eventList

                    setOnMouseClicked {
                        controller.handleEventListClicked(selectedItem)
                    }
                }
            }
        }

        tab("Nodes") {
            scrollpane {
                padding = Insets(10.0)

                label {
                    bind(controller.nodesProperty())
                }
            }
        }
    }
}