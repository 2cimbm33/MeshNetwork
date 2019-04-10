package uni.cimbulka.network.simulator.gui.views

import javafx.scene.layout.AnchorPane
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.MainController
import uni.cimbulka.network.simulator.gui.events.SwitchViewEvent

class MainView : View("Main View") {
    private val controller: MainController by inject()
    private val startSimulationView: StartSimulationView by inject()
    private val runView: RunView by inject()

    override val root = borderpane {
        prefWidth = 400.0
        prefHeight = 450.0

        top = label("Simulator") {
            style = "-fx-font-weight: bold"
            font = Font.font(25.0)
            textAlignment = TextAlignment.CENTER
        }

        center = anchorpane {
            paddingAll = 15

            add(startSimulationView)

            AnchorPane.setLeftAnchor(startSimulationView.root, 0.0)
            AnchorPane.setTopAnchor(startSimulationView.root, 0.0)
            AnchorPane.setRightAnchor(startSimulationView.root, 0.0)
            AnchorPane.setBottomAnchor(startSimulationView.root, 0.0)
        }
    }

    init {
        subscribe<SwitchViewEvent<StartSimulationView>> {
            val node = startSimulationView.root

            AnchorPane.setLeftAnchor(node, 0.0)
            AnchorPane.setTopAnchor(node, 0.0)
            AnchorPane.setRightAnchor(node, 0.0)
            AnchorPane.setBottomAnchor(node, 0.0)

            val center = root.center
            if (center is AnchorPane) {
                center.children.clear()
                center.add(node)
            } else {
                root.center = node
            }
        }

        subscribe<SwitchViewEvent<RunView>> {
            val node = runView.root

            AnchorPane.setLeftAnchor(node, 0.0)
            AnchorPane.setTopAnchor(node, 0.0)
            AnchorPane.setRightAnchor(node, 0.0)
            AnchorPane.setBottomAnchor(node, 0.0)

            val center = root.center
            if (center is AnchorPane) {
                center.children.clear()
                center.add(node)
            } else {
                root.center = node
            }
        }
    }
}