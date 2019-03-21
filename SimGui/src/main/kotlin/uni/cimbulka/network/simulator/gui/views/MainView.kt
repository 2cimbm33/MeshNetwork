package uni.cimbulka.network.simulator.gui.views

import javafx.collections.FXCollections
import tornadofx.View
import tornadofx.borderpane
import tornadofx.listview
import tornadofx.selectedItem
import uni.cimbulka.network.simulator.gui.FileLoader
import uni.cimbulka.network.simulator.gui.models.Report
import uni.cimbulka.network.simulator.mesh.MeshSimulator

class MainView : View("Main View") {
    private val snapshotView: SnapshotView by inject()
    private lateinit var report: Report
    private val listViewItems = FXCollections.observableArrayList<String>()

    override val root = borderpane {
        prefWidth = 1024.0
        prefHeight = 640.0

        center = snapshotView.root

        left = listview<String> {
            prefWidth = 400.0
            items = listViewItems

            setOnMouseClicked {
                val item = this.selectedItem ?: return@setOnMouseClicked
                if (::report.isInitialized) {
                    val event = report.events[item] ?: return@setOnMouseClicked
                    snapshotView.display(event)
                }
            }
        }
    }

    init {
        runAsync {
            val simulator = MeshSimulator()
            simulator.run()

            Report.fromJson(FileLoader.readFile("simulationReport.json"))
        } ui {
            this.report = it
            listViewItems.addAll(it.events.keys)
        }
    }

}