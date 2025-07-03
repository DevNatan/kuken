package gg.kuken.http.websocket

typealias WebSocketOp = Int

object WebSocketOpCodes {

    const val INSTANCE_FETCH_LOGS: WebSocketOp = 0

    const val INSTANCE_RUN_COMMAND: WebSocketOp = 1

    const val INSTANCE_STATS_STREAMING: WebSocketOp = 2
}
