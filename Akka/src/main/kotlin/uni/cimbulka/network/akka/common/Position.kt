package uni.cimbulka.network.akka.common

data class Position(var x: Double = 0.0, var y: Double = 0.0) {

    fun distance(other: Position): Double {
        return Math.sqrt(Math.pow(other.x - x, 2.0) + Math.pow(other.y - y, 2.0))
    }
}