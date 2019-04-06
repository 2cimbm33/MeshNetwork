package uni.cimbulka.network.data

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import uni.cimbulka.network.NetworkConstants

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "dataType")
@JsonSubTypes(
        Type(value = ApplicationData::class, name = NetworkConstants.APPLICATION_DATA),
        Type(value = EmptyData::class, name = NetworkConstants.EMPTY_DATA),
        Type(value = HandshakeData::class, name = NetworkConstants.HANDSHAKE_RESPONSE_DATA),
        Type(value = UpdateData::class, name = NetworkConstants.UPDATE_DATA)
)
abstract class BaseData