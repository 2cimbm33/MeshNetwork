package uni.cimbulka.network.simulator.gui.views.dialogs

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.control.*
import tornadofx.*
import uni.cimbulka.network.simulator.common.Position
import uni.cimbulka.network.simulator.gui.models.AddNodeDialogResult

class AddNodeDialog : Dialog<AddNodeDialogResult>() {
    init {
        title = "Add Node dialog"
        headerText = "Add node"

        val addButtonType = ButtonType("Add", ButtonBar.ButtonData.OK_DONE)
        dialogPane.buttonTypes.addAll(addButtonType, ButtonType.CANCEL)

        val txtName = TextField().apply { promptText = "Username" }
        val txtPosX = TextField()
        val txtPosY = TextField()
        val txtDelay = TextField()

        dialogPane.content = gridpane {
            hgap = 10.0
            vgap = 10.0
            padding = Insets(20.0, 150.0, 10.0, 10.0)

            add(Label("Name:"), 0, 0)
            add(txtName, 0, 1)
            add(Label("Position X:"), 1, 0)
            add(txtPosX, 1, 1)
            add(Label("Position Y:"), 2, 0)
            add(txtPosY, 2, 1)
            add(Label("Delay:"), 3, 0)
            add(txtDelay, 3, 1)
        }

        dialogPane.lookupButton(addButtonType).apply {
            isDisable = true

            fun validate() {
                isDisable = when {
                    txtName.text.isBlank() -> true
                    txtPosX.text.toDoubleOrNull() == null -> true
                    else -> txtPosY.text.toDoubleOrNull() == null
                }
            }

            txtName.textProperty().onChange { validate() }
            txtPosX.textProperty().onChange { validate() }
            txtPosY.textProperty().onChange { validate() }
        }

        Platform.runLater { txtName.requestFocus() }

        setResultConverter {
            if (it == addButtonType) {
                txtDelay.text.toDoubleOrNull()?.let { delay ->
                    AddNodeDialogResult(txtName.text, Position(txtPosX.text.toDouble(), txtPosY.text.toDouble()), delay)
                } ?: AddNodeDialogResult(txtName.text, Position(txtPosX.text.toDouble(), txtPosY.text.toDouble()))
            } else {
                null
            }
        }
    }
}