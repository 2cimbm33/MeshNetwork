package uni.cimbulka.network.simulator.gui.views.dialogs

import javafx.collections.FXCollections
import javafx.scene.control.*
import tornadofx.*
import uni.cimbulka.network.simulator.gui.models.RemoveNodeDialogResult

class RemoveNodeDialog(names: List<String>) : Dialog<RemoveNodeDialogResult>() {
    init {
        title = "Remove Node dialog"
        headerText = "Remove node"

        val removeButtonType = ButtonType("Remove", ButtonBar.ButtonData.OK_DONE)
        dialogPane.buttonTypes.addAll(removeButtonType, ButtonType.CANCEL)

        val namesChoiceBox = ChoiceBox<String>().apply {
            items = FXCollections.observableList(names)
        }

        dialogPane.content = gridpane {
            add(Label("Names:"), 0, 0)
            add(namesChoiceBox, 0, 1)
        }

        setResultConverter {
            if (it == removeButtonType) {
                RemoveNodeDialogResult(namesChoiceBox.selectionModel.selectedItem)
            } else {
                null
            }
        }
    }
}