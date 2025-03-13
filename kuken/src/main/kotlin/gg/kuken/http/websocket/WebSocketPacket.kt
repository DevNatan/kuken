package gg.kuken.http.websocket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import gg.kuken.http.websocket.WebSocketPacket.Companion.DATA
import gg.kuken.http.websocket.WebSocketPacket.Companion.OP

@Serializable
data class WebSocketPacket(
    @SerialName(OP) val op: Int,
    @SerialName(DATA) val data: JsonObject? = null
) {

    companion object {

        internal const val OP = "o"
        internal const val DATA = "d"

        const val TARGET_ID = "tid"
        const val VALUE = "v"
    }
}

data class WebSocketPacketContext(
    val packet: WebSocketPacket,
    val session: WebSocketSession
)

fun WebSocketPacketContext.stringData(key: String): String? {
    return (packet.data?.get(key) as? JsonPrimitive)?.contentOrNull
}

@Serializable
data class WebSocketResponse<T>(
    @SerialName(OP) val op: WebSocketOp,
    @SerialName(DATA) val data: T
)

suspend inline fun <reified T> WebSocketPacketContext.respond(data: T, code: Int = packet.op) {
    session.send(WebSocketResponse(code, data))
}
