package org.katan.http.websocket

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import java.nio.channels.ClosedChannelException
import java.util.Collections
import kotlin.reflect.jvm.jvmName

class WebSocketManager : CoroutineScope by CoroutineScope(
    SupervisorJob() + CoroutineName(WebSocketManager::class.jvmName)
) {

    companion object {
        private val logger = LogManager.getLogger(WebSocketManager::class.java)
        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    private val sessions = Collections.synchronizedSet<WebSocketSession>(linkedSetOf())
    private val generatedId = atomic(0)
    private val handlers = mutableMapOf<WebSocketOp, MutableList<WebSocketPacketEventHandler>>()

    suspend fun connect(connection: DefaultWebSocketServerSession) {
        val session = WebSocketSession(generatedId.getAndIncrement(), connection)
        sessions.add(session)
        logger.debug(
            "WebSocket session {} connected @ {}",
            session.id,
            session.connection.call.request.local.remoteHost
        )

        try {
            for (frame in connection.incoming) {
                if (frame !is Frame.Text) continue

                try {
                    val packet: WebSocketPacket = json.decodeFromString(frame.readText())
                    packetReceived(packet, session)
                } catch (e: SerializationException) {
                    logger.error("Failed to handle WebSocket packet", e)
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            logger.debug("WebSocket session receive channel closed")
        } catch (e: Throwable) {
            val closeReason = session.connection.closeReason.await()
            logger.error("WebSocket session handling uncaught error: $closeReason", e)
        } finally {
            logger.debug("WebSocket session ${session.id} closed")
            detach(session)
        }
    }

    private suspend fun packetReceived(packet: WebSocketPacket, session: WebSocketSession) {
        val context = WebSocketPacketContext(packet, session)

        handlers[packet.op]?.forEach { handler ->
            handler.apply {
                context.handle()
            }
        }
    }

    private suspend fun detach(session: WebSocketSession) {
        try {
            session.connection.close()
        } catch (_: ClosedChannelException) {
        }

        sessions.remove(session)
    }

    fun register(op: WebSocketOp, handler: WebSocketPacketEventHandler) {
        handler.coroutineContext = Job() + CoroutineName(
            "%s-%s".format(
                op,
                handler::class.simpleName ?: "unknown-websocket-handler"
            )
        )
        handlers.computeIfAbsent(op) { mutableListOf() }.add(handler)
    }
}
