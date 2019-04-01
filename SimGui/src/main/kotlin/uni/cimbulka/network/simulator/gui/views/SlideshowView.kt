package uni.cimbulka.network.simulator.gui.views

import tornadofx.View
import tornadofx.button
import tornadofx.stackpane
import uni.cimbulka.network.simulator.gui.controllers.SlideshowController

class SlideshowView : View("Slideshow View") {
    private val controller: SlideshowController by inject()
    private val graphView: GraphView by inject()

    override val root = stackpane {
        button("Play") {
            setOnMouseClicked {
                if (!controller.playing && controller.report != null) {
                    controller.play()
                }
            }
        }
        add(graphView)
    }


}