package uni.cimbulka.network.simulator.gui.views

import javafx.scene.Parent
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

        center = getCenter(startSimulationView)
    }

    init {
        subscribe<SwitchViewEvent<StartSimulationView>> {
            root.center = getCenter(startSimulationView)
        }

        subscribe<SwitchViewEvent<RunView>> {
            root.center = getCenter(runView)
        }
    }

    private fun getCenter(view: View): Parent {
        return anchorpane {
            paddingAll = 15
            add(view)

            AnchorPane.setLeftAnchor(view.root, 0.0)
            AnchorPane.setTopAnchor(view.root, 0.0)
            AnchorPane.setRightAnchor(view.root, 0.0)
            AnchorPane.setBottomAnchor(view.root, 0.0)
        }
    }
}