package org.katan.http

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.util.logging.error
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.katan.http.response.HttpError
import org.katan.http.response.ValidationException
import org.katan.model.Snowflake
import org.katan.model.toSnowflake
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalSerializationApi::class)
fun Application.installDefaultFeatures(isDevelopmentMode: Boolean, json: Json) {
    install(RoutingRoot)
    install(Resources)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(DataConversion) {
        convert<Snowflake> {
            decode { values -> values.first().toLong().toSnowflake() }
            encode { snowflake -> listOf(snowflake.toString()) }
        }
    }
    install(CallLogging) {
        level = if (isDevelopmentMode) Level.DEBUG else Level.INFO
        logger = LoggerFactory.getLogger("Ktor")
    }
    install(ContentNegotiation) {
        json(Json(from = json) {
            explicitNulls = false
        })
    }
    install(StatusPages) {
        exception<ValidationException> { call, exception ->
            call.respond(
                status = HttpStatusCode.UnprocessableEntity,
                message = exception.data
            )
        }

        exception<SerializationException> { call, exception ->
            if (isDevelopmentMode) call.application.log.error(exception)
            call.respond(
                status = HttpStatusCode.UnprocessableEntity,
                message = HttpError.Generic(exception.localizedMessage)
            )
        }

        exception<BadRequestException> { call, exception ->
            call.respond(
                status = HttpStatusCode.UnprocessableEntity,
                message = HttpError.Generic(exception.localizedMessage)
            )
        }

        exception<HttpException> { call, exception ->
            if (isDevelopmentMode) call.application.log.error(exception)
            call.respond(
                status = exception.status,
                message = HttpError(
                    code = exception.code,
                    message = exception.message.orEmpty(),
                    details = exception.details
                )
            )
        }

        exception<Throwable> { call, exception ->
            call.application.log.error("Unhandled exception", exception)
            call.respond(
                HttpStatusCode.InternalServerError,
                HttpError.Generic("Internal server error: ${exception::class.simpleName}")
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
