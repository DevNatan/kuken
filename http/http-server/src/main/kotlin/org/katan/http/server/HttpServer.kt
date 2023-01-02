package org.katan.http.server

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.EngineConnectorBuilder
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.http.di.HttpModuleRegistry
import org.katan.http.installDefaultFeatures
import org.katan.http.server.routes.serverInfo
import org.katan.http.websocket.WebSocketManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HttpServer(
    private val port: Int
) : CoroutineScope by CoroutineScope(CoroutineName("HttpServer")), KoinComponent {

    companion object {
        private val logger: Logger = LogManager.getLogger(HttpServer::class.java)
    }

    private val httpModuleRegistry by inject<HttpModuleRegistry>()
    private val webSocketManager by inject<WebSocketManager>()

    init {
        System.setProperty("io.ktor.development", "true")
    }

    private var shutdownPending by atomic(false)

    private val engine: ApplicationEngine by lazy {
        embeddedServer(
            factory = CIO,
            module = { setupEngine(this) },
            connectors = arrayOf(createHttpConnector())
        )
    }

    fun start() {
        engine.addShutdownHook {
            stop()
        }

        for (connector in engine.environment.connectors)
            logger.info("Listening on ${connector.host}:${connector.port} (${connector.type.name.lowercase()})")

        engine.start(wait = true)
    }

    fun stop() {
        if (shutdownPending) return

        shutdownPending = true
        engine.stop(
            gracePeriodMillis = 1000,
            timeoutMillis = 5000
        )
        shutdownPending = false
    }

    private fun setupEngine(app: Application) {
        app.installDefaultFeatures()
        app.setupWebsocket()
        app.routing { serverInfo() }
        for (module in httpModuleRegistry) {
            module.install(app)
            for ((op, handler) in module.webSocketHandlers())
                webSocketManager.register(op, handler)
        }
    }

    private fun Application.setupWebsocket() = routing {
        webSocket {
            webSocketManager.connect(this)
        }
    }

    private fun createHttpConnector() = EngineConnectorBuilder().apply {
        host = "0.0.0.0"
        port = this@HttpServer.port
    }
}
