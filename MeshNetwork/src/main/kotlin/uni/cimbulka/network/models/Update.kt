package uni.cimbulka.network.models

data class Update @JvmOverloads constructor(var first: Device? = null, var second: Device? = null, var action: Int = -1) {
    constructor(nodes: Pair<Device, Device>, action: Int) : this(nodes.first, nodes.second, action)

        override fun equals(other: Any?): Boolean = when (other) {
        is Update -> action == other.action &&
                (first == other.first || first == other.second) &&
                (second == other.first || second == other.second)
        else -> false
    }

    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        result = 31 * result + action
        return result
    }

    companion object {
        const val CONNECTION_CREATED = 100
        const val CONNECTION_DELETED = 101
    }
}
