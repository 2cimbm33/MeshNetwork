package uni.cimbulka.network.simulator.gui.views

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.FileLoader
import uni.cimbulka.network.simulator.gui.models.Report
import uni.cimbulka.network.simulator.mesh.Simulation1
import uni.cimbulka.network.simulator.mesh.Simulation2
import uni.cimbulka.network.simulator.mesh.Simulation3
import uni.cimbulka.network.simulator.mesh.Simulation4

class MainView : View("Main View") {
    private val snapshotView: SnapshotView by inject()
    private lateinit var report: Report
    private val listViewItems = FXCollections.observableArrayList<String>()
    private val nodes = Label()
    private val stats = Label()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        tab("Events") {
            borderpane {
                prefWidth = 1024.0
                prefHeight = 640.0

                center = snapshotView.root

                left = listview<String> {
                    prefWidth = 400.0
                    items = listViewItems

                    setOnMouseClicked {
                        val item = this.selectedItem ?: return@setOnMouseClicked
                        if (::report.isInitialized) {
                            val snapshot = report.events[item] ?: return@setOnMouseClicked
                            snapshotView.display(snapshot)
                        }
                    }
                }
            }
        }

        tab("Nodes") {
            scrollpane {
                padding = Insets(10.0)

                add(nodes)
            }
        }

        tab("Stats") {
            scrollpane {
                padding = Insets(10.0)

                add(stats)
            }
        }
    }

    init {
        runAsync {
            val simulator = Simulation4()
            simulator.run()

            Report.fromJson(FileLoader.readFile("simulationReport.json"))
        } ui {
            this.report = it
            listViewItems.addAll(it.events.keys)

            val mapper = ObjectMapper()
            val builder = StringBuilder()

            report.nodes.forEach {
                builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
                builder.appendln()
            }
            nodes.text = builder.toString()

            builder.clear()
            report.aggregation.stats.forEach {
                builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
                builder.appendln()
            }
            stats.text = builder.toString()
        }
    }

}