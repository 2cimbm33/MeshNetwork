package uni.cimbulka.network.simulator.mobility

object MobilityManager {
    private val rules = mutableMapOf<String, MobilityRule>()

    fun getRule(id: String) = rules[id]

    fun addRule(id: String, rule: MobilityRule) {
        rules[id] = rule
    }
}