package uni.cimbulka.network.helpers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.*

class UUIDJsonSerializer : JsonSerializer<UUID>() {
    override fun serialize(value: UUID?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (value == null) {
            gen?.writeNull()
        } else {
            gen?.writeString(value.toString())
        }
    }
}

class UUIDJsonDeserilazer : JsonDeserializer<UUID>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): UUID {
        return UUID.fromString(p?.valueAsString)
    }

}