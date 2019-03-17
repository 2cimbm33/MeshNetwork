package uni.cimbulka.network.models

data class RouteSegment @JvmOverloads constructor(
        var start: Device? = null,
        var end: Device? = null
)