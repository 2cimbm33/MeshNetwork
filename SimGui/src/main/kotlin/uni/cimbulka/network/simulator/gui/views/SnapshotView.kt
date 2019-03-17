package uni.cimbulka.network.simulator.gui.views

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import tornadofx.*
import uni.cimbulka.network.simulator.gui.models.Event

class SnapshotView : View() {
    private val name = Label()
    private val time = Label()
    private val args = Label()

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

        center = scrollpane {
            AnchorPane.setTopAnchor(this, 0.0)
            AnchorPane.setLeftAnchor(this, 0.0)
            AnchorPane.setRightAnchor(this, 0.0)
            AnchorPane.setBottomAnchor(this, 0.0)

            padding = Insets(10.0)

            add(args)
        }
    }

    fun display(event: Event) {
        val text = ObjectMapper().apply {
            enable(SerializationFeature.INDENT_OUTPUT)
        }.writerWithDefaultPrettyPrinter().writeValueAsString(event.args)

        name.text = event.name
        time.text = event.time.toString()
        args.text = text
    }
}