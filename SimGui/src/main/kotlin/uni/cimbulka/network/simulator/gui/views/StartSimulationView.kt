package uni.cimbulka.network.simulator.gui.views

import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.IntegerStringConverter
import javafx.util.converter.NumberStringConverter
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.StartSimulationController

class StartSimulationView : View("Start Simulation") {
    private val controller: StartSimulationController by inject()

    override val root = form {
        fieldset("Nodes") {
            field("Create probability") {
                slider(0..100) {
                    valueProperty().bindBidirectional(controller.createProbabilityProperty())

                    isShowTickMarks = true
                    majorTickUnit = 10.0
                    blockIncrement = 5.0
                }
            }

            field("Probability") {
                label {
                    bind(controller.createProbabilityProperty(), true, NumberStringConverter())
                }
            }

            field("Number of prefab nodes") {
                textfield().textProperty().bindBidirectional(controller.numberOfPrefabsProperty(), IntegerStringConverter())
            }
        }

        fieldset("Other") {
            field("Width") {
                textfield().textProperty().bindBidirectional(controller.widthProperty(), DoubleStringConverter())
            }

            field("Height") {
                textfield().textProperty().bindBidirectional(controller.heightProperty(), DoubleStringConverter())
            }

            field("Time") {
                textfield().textProperty().bindBidirectional(controller.timeProperty(), DoubleStringConverter())
            }
        }

        button("Run") {
            action(controller::runSimulation)
        }
    }
}
