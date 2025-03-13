package org.katan.http.websocket

import gg.kuken.http.websocket.WebSocketPacketContext
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

abstract class WebSocketPacketEventHandler : CoroutineScope {

    override lateinit var coroutineContext: CoroutineContext internal set

    abstract suspend fun WebSocketPacketContext.handle()
}
