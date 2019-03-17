package uni.cimbulka.network.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException
import uni.cimbulka.network.helpers.UUIDJsonDeserilazer
import uni.cimbulka.network.helpers.UUIDJsonSerializer
import java.util.*

data class Device @JvmOverloads constructor(
        @JsonSerialize(using = UUIDJsonSerializer::class)
        @JsonDeserialize(using = UUIDJsonDeserilazer::class)
        var id: UUID? = null,
        var name: String = "") {

    var isInNetwork: Boolean = false

    override fun toString(): String {
        return ObjectMapper().apply {
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
            ObjectMapper().readValue(json, Device::class.java)
        } catch (e: UnrecognizedPropertyException) {
            null
        }
    }
}
