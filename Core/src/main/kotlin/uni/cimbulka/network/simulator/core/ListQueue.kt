package uni.cimbulka.network.simulator.core

import uni.cimbulka.network.simulator.core.interfaces.Comparable
import uni.cimbulka.network.simulator.core.interfaces.EventInterface
import uni.cimbulka.network.simulator.core.models.OrderedSet
import java.util.*

class ListQueue<T : Comparable> : OrderedSet<T>() {
    private val elements = Vector<T>()

    override val size: Int
        get() = elements.size

    override fun insert(element: T) {
        var i = 0

        while (i < size && (elements[i] as EventInterface) < element) {
            i++
        }

        elements.insertElementAt(element, i)
    }

    override fun peek(): T? {
        if (elements.isEmpty()) return null
        return elements.firstElement();
    }

    override fun removeFirst(): T? {
        if (elements.isEmpty()) return null

        val element = elements.firstElement()
        elements.remove(element)
        return element
    }

    override fun remove(element: T): T? {
        for (i in 0..(size - 1)) {
            if (elements[i] == element) {
                val el = elements[i]
                elements.removeElementAt(i)
                return el
            }
        }

        return null
    }

    override fun removeAll() {
        elements.clear()
    }
}