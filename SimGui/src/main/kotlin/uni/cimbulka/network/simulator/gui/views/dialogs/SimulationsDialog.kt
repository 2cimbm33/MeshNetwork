package uni.cimbulka.network.simulator.gui.views.dialogs

import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.dialogs.SimulationsController
import uni.cimbulka.network.simulator.gui.events.CloseEvent
import uni.cimbulka.network.simulator.gui.models.Simulation

class SimulationsDialog : View("Simulations") {
    private val controller: SimulationsController by inject()

    override val root = vbox {
        prefHeight = 700.0
        prefWidth = 400.0

        listview<Simulation> {
            items = controller.simulations

            setOnMouseClicked {
                when (it.clickCount) {
                    1 -> controller.handleSelectionChanged(selectedItem)
                    2 -> controller.handleOpenClicked()
                }

            }
        }

        button("Open") {
            disableWhen(controller.disabledProperty())
            action(controller::handleOpenClicked)
        }

        button("Close") {
            action(controller::handleCloseClicked)
        }
    }

    init {
        subscribe<CloseEvent<SimulationsDialog>> {
            close()
        }
    }
}