package gg.kuken.http

import gg.kuken.KukenConfig
import gg.kuken.http.exception.ResourceException
import gg.kuken.http.util.ValidationException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.util.logging.error
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSerializationApi::class)
internal fun Application.installDefaultFeatures(config: KukenConfig) {
    install(Resources)
    install(DefaultHeaders)
    install(AutoHeadResponse)

    install(CallLogging) {
        level = if (config.devMode) Level.DEBUG else Level.INFO
        logger = LoggerFactory.getLogger("Ktor")
    }

    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
        })
    }

    install(StatusPages) {
        exception<ResourceException> { call, exception ->
            call.respond(
                status = exception.code,
                message = exception.error
            )
        }

        exception<ValidationException> { call, exception ->
            call.respond(
                status = HttpStatusCode.UnprocessableEntity,
                message = exception.data
            )
        }

        exception<SerializationException> { call, exception ->
            if (config.devMode) call.application.log.error(exception)
            call.respond(HttpStatusCode.UnprocessableEntity)
        }

        exception<BadRequestException> { call, exception ->
            call.respond(HttpStatusCode.UnprocessableEntity)
        }

        exception<Throwable> { call, exception ->
            call.application.log.error("Unhandled exception", exception)
            call.respond(
                HttpStatusCode.InternalServerError,
                exception.message.toString()
            )
        }
    }

    install(CORS) {
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Put)
        allowXHttpMethodOverride()
        allowHeader(HttpHeaders.Authorization)
        anyHost()
    }

    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
    }
}
