package uni.cimbulka.network.simulator.gui.views

import javafx.geometry.Insets
import javafx.scene.control.TabPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.MainController

class MainView : View("Main View") {
    private val controller: MainController by inject()
    private val snapshotView: SnapshotView by inject()
    private val slideshowView: SlideshowView by inject()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        prefWidth = 1024.0
        prefHeight = 640.0

        tab("Main") {
            borderpane {
                left = vbox {
                    button("Run Simulation1") {
                        userData = "Simulation1"
                        action {
                            controller.runSimulation("Simulation1")
                        }
                    }

                    button("Run Simulation2") {
                        userData = "Simulation1"
                        action {
                            controller.runSimulation("Simulation2")
                        }
                    }

                    button("Run Simulation3") {
                        userData = "Simulation1"
                        action {
                            controller.runSimulation("Simulation3")
                        }
                    }

                    button("Run Simulation4") {
                        userData = "Simulation1"
                        action {
                            controller.runSimulation("Simulation4")
                        }
                    }

                }

                center = button("Open Simulation") {
                    action(controller::openSimulationPicker)
                }
            }
        }

        tab("Events") {
            borderpane {
                center = snapshotView.root

                left = listview<Int> {
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

        tab("Stats") {
            scrollpane {
                padding = Insets(10.0)

                label {
                    bind(controller.statsProperty())
                }
            }
        }

        tab("Slideshow") {
            scrollpane {
                add(slideshowView)
            }
        }
    }
}