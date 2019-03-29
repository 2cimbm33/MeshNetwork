package uni.cimbulka.network.simulator.gui.views

import tornadofx.View
import tornadofx.button
import tornadofx.stackpane
import uni.cimbulka.network.simulator.gui.models.Report

class SlideshowView : View("Slideshow View") {
    private val graphView: GraphView by inject()
    private var playing = false
    lateinit var report: Report

    override val root = stackpane {
        button("Play") {
            setOnMouseClicked {
                if (!playing && ::report.isInitialized) {
                    play(report)
                }
            }
        }
        add(graphView)
    }

    fun play(report: Report) {
        val delay = 100

        playing = true
        report.events.values.forEach { snapshot ->
            runAsync {
                Thread.sleep(delay.toLong())
            } ui {
                graphView.draw(snapshot.nodes, snapshot.connections)
            }
        }
        playing = false
    }
}