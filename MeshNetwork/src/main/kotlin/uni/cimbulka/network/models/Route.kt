package uni.cimbulka.network.models

data class Route @JvmOverloads constructor(
        val segments: MutableList<RouteSegment> = mutableListOf()
) {

    @JvmOverloads
    fun getIndex(device: Device, end: Boolean = true): Int {
        segments.forEachIndexed { index, segment ->
            if (end) {
                if (segment.end == device) {
                    return index
                }
            } else {
                if (segment.start == device) {
                    return index
                }
            }
        }

        return -1
    }
}