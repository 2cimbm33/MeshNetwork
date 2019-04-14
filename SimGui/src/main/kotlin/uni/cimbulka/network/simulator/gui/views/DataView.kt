package uni.cimbulka.network.simulator.gui.views

import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import tornadofx.*
import uni.cimbulka.network.simulator.gui.controllers.DataController

class DataView : View("My View") {
    private val controller: DataController by inject()

    override val root = borderpane {
        top = button("GET") {
            action { controller.getNumberOfEvents("tenmin-v2") }
        }

        center = barchart<String, Number>("Number of events", CategoryAxis(), NumberAxis()) {
            series("Events", controller.numberOfEvents)
        }
    }
}
