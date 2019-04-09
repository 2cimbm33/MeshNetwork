package uni.cimbulka.network.simulator.gui.views

import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.IntegerStringConverter
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.StartSimulationController

class StartSimulationView : View("Start Simulation") {
    private val controller: StartSimulationController by inject()

    override val root = form {
        fieldset("Nodes") {
            field("Number of nodes") {
                textfield().textProperty().bindBidirectional(controller.numberOfNodesProperty(), IntegerStringConverter())
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
