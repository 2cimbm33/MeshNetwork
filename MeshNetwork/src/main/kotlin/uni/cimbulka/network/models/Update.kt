package uni.cimbulka.network.models

data class Update(val nodes: Pair<Device, Device>, val action: Int) {

    constructor(first: Device, second: Device, action: Int) : this(first to second, action)

    override fun equals(other: Any?): Boolean = when (other) {
        is Update -> action == other.action &&
                (nodes.first == other.nodes.first || nodes.first == other.nodes.second) &&
                (nodes.second == other.nodes.first || nodes.second == other.nodes.second)
        else -> false
    }

    override fun hashCode(): Int {
        var result = nodes.first.hashCode()
        result = 31 * result + nodes.second.hashCode()
        result = 31 * result + action
        return result
    }

    companion object {
        const val CONNECTION_CREATED = 100
        const val CONNECTION_DELETED = 101
    }
}
