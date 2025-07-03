package gg.kuken.http.websocket

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.serialization.json.Json

class WebSocketSession internal constructor(
    val id: Int,
    val connection: DefaultWebSocketServerSession,
    @Transient private val json: Json,
) {

    suspend fun send(message: WebSocketResponse<*>) {
        connection.outgoing.send(Frame.Text(json.encodeToString(message)))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebSocketSession

        if (id != other.id) return false
        if (connection != other.connection) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + connection.hashCode()
        return result
    }
}
