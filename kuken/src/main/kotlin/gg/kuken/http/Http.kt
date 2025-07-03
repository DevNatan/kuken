package gg.kuken.http

import gg.kuken.KukenConfig
import gg.kuken.http.modules.account.AccountHttpModule
import gg.kuken.http.modules.auth.AuthHttpModule
import gg.kuken.http.websocket.WebSocketManager
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.embeddedServer
import kotlinx.atomicfu.atomic
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal object Http : KoinComponent {

    private const val STOP_GRACE_PERIOD_MILLIS: Long = 1000
    private const val TIMEOUT_MILLIS: Long = 5000

    private val appConfig: KukenConfig by inject()

    private var shutdownPending = atomic(false)
    private val engine: EmbeddedServer<*, *> = createServer()
    private val webSocketManager = WebSocketManager(json = Json)

    init {
        if (appConfig.devMode) {
            System.setProperty("io.ktor.development", "true")
        }
    }

    suspend fun start() {
        engine.addShutdownHook(::stop)
        engine.startSuspend(wait = true)
    }

    fun stop() {
        if (shutdownPending.compareAndSet(expect = false, update = false)) {
            engine.stop(STOP_GRACE_PERIOD_MILLIS, TIMEOUT_MILLIS)
        }
    }

    private fun Application.registerHttpModules() {
        for (module in createHttpModules().sortedByDescending(HttpModule::priority)) {
            module.install(this)

            for ((op, handler) in module.webSocketHandlers())
                webSocketManager.register(op, handler)
        }
    }

    private fun configureApplication(app: Application): Unit = with(app) {
        installDefaultFeatures(appConfig)
        registerHttpModules()
    }

    private fun createServer() = embeddedServer(
        factory = CIO,
        host = appConfig.http.host,
        port = appConfig.http.port,
        watchPaths = listOf("classes").takeIf { appConfig.devMode }.orEmpty(),
        module = { configureApplication(this) },
    )

    fun createHttpModules() = setOf(
        AuthHttpModule,
        AccountHttpModule
    )
}
