package uni.cimbulka.network.simulator.core.models

import uni.cimbulka.network.simulator.core.interfaces.Comparable

abstract class OrderedSet<T : Comparable> {
    abstract val size: Int
    abstract fun insert(element: T)
    abstract fun peek(): T?
    abstract fun removeFirst(): T?
    abstract fun remove(element: T): T?
    abstract fun removeAll()
}