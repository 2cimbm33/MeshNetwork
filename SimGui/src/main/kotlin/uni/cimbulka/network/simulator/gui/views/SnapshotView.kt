package uni.cimbulka.network.simulator.gui.views

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.models.Snapshot

class SnapshotView : View() {
    private val name = Label()
    private val time = Label()
    private val args = Label()
    private val nodes = Label()

    override val root = borderpane {
        top = hbox {
            alignment = Pos.CENTER
            padding = Insets(10.0)
            spacing = 20.0

            hbox {
                spacing = 5.0

                label("Name:") {
                    style = "-fx-font-weight: bold"
                }
                add(name)
            }

            hbox {
                spacing = 5.0

                label("Time:") {
                    style = "-fx-font-weight: bold"
                }
                add(time)
            }
        }

        center = tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tab("Args") {
                scrollpane {
                    padding = Insets(10.0)

                    add(args)
                }
            }

            tab("Nodes") {
                scrollpane {
                    padding = Insets(10.0)

                    add(nodes)
                }
            }
        }


    }

    fun display(snapshot: Snapshot) {
        val mapper = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
        val event = snapshot.event ?: return

        val text = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event.args)

        name.text = event.name
        time.text = event.time.toString()
        args.text = text

        val builder = StringBuilder()
        snapshot.nodes.forEach {
            builder.appendln(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            builder.appendln()
        }
        nodes.text = builder.toString()
    }
}