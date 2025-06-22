package org.katan.http.response

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.util.pipeline.PipelineContext
import jakarta.validation.Validator
import org.katan.http.HttpException

suspend inline fun RoutingContext.respond(
    response: Any,
    status: HttpStatusCode = HttpStatusCode.OK
): Unit = call.respond(status, response)

suspend inline fun <reified T : Any> ApplicationCall.receiveValidating(validator: Validator): T =
    receiveNullable<T>()?.also(validator::validateOrThrow).let(::requireNotNull)

fun respondError(
    error: HttpError,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
    cause: Throwable? = null
): Nothing = throw HttpException(error.code, error.message, error.details, status, cause)
