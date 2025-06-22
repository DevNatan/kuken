package org.katan.http.server

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.EngineConnectorBuilder
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.KatanConfig
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.http.installDefaultFeatures
import org.katan.http.server.routes.serverInfo
import org.katan.http.websocket.WebSocketManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.reflect.jvm.jvmName

private typealias CIOEmbeddedServer = EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>

class HttpServer(private val host: String, private val port: Int) :
    CoroutineScope by CoroutineScope(CoroutineName(HttpServer::class.jvmName)), KoinComponent {

    companion object {
        private const val STOP_GRACE_PERIOD_MILLIS: Long = 1000
        private const val TIMEOUT_MILLIS: Long = 5000

        private val logger: Logger = LogManager.getLogger(HttpServer::class.java)
    }

    private val config by inject<KatanConfig>()
    private val webSocketManager by inject<WebSocketManager>()
    private var shutdownPending by atomic(false)
    private val server: CIOEmbeddedServer = createServer()

    init {
        System.setProperty("io.ktor.development", config.isDevelopment.toString())
    }

    fun start() {
        server.addShutdownHook(::stop)

        for (connector in server.engineConfig.connectors)
            logger.debug("Listening on {}", connector)

        server.start(wait = true)
    }

    fun stop() {
        if (shutdownPending) return

        shutdownPending = true
        server.stop(STOP_GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
        shutdownPending = false
    }

    private fun configureApplication(app: Application) = with(app) {
        installDefaultFeatures(
            isDevelopmentMode = config.isDevelopment,
            json = get<Json>(),
        )
        routing {
            webSocket { webSocketManager.connect(this) }
            serverInfo()
        }
        registerModules(app)
    }

    private fun registerModules(app: Application) {
        val registry = get<HttpModuleRegistry>()
        for (module in registry.modules.sortedByDescending(HttpModule::priority)) {
            module.install(app)
            for ((op, handler) in module.webSocketHandlers())
                webSocketManager.register(op, handler)
        }
    }

    private fun createServer() = embeddedServer(
        factory = CIO,
        module = { configureApplication(this) },
        connectors = arrayOf(
            EngineConnectorBuilder().apply {
                host = this@HttpServer.host
                port = this@HttpServer.port
            }
        ),
        watchPaths = listOf("classes").takeIf { config.isDevelopment }.orEmpty(),
    )
}
