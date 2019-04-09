package uni.cimbulka.network.simulator.mesh.reporting

data class Connection(val first: String, val second: String) {
    operator fun contains(other: String): Boolean {
        return first == other || second == other
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Connection -> other.first in this && other.second in this
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }
}