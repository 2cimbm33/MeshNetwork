package uni.cimbulka.network.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import uni.cimbulka.network.helpers.UUIDJsonDeserializer
import uni.cimbulka.network.helpers.UUIDJsonSerializer
import java.util.*

data class Device(
        @JsonSerialize(using = UUIDJsonSerializer::class)
        @JsonDeserialize(using = UUIDJsonDeserializer::class)
        var id: UUID,
        var name: String) {

    var inNetwork: Boolean = false
    val communications = mutableMapOf<String, String>()

    fun merge(other: Device): Boolean {
        if (id != other.id) return false

        other.communications.entries.forEach {
            communications.putIfAbsent(it.key, it.value)
        }

        return true
    }

    override fun toString(): String {
        return jacksonObjectMapper().apply {
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }.writeValueAsString(this)
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Device -> other.id == id
            else -> false
        }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String) = try {
            jacksonObjectMapper().readValue<Device>(json)
        } catch (e: UnrecognizedPropertyException) {
            null
        }
    }
}
